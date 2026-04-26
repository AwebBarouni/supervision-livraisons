package com.supervision.livraisons.repository;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.supervision.livraisons.data.local.DeliveryDao;
import com.supervision.livraisons.data.local.DeliveryEntity;
import com.supervision.livraisons.data.local.MessageDao;
import com.supervision.livraisons.data.local.MessageEntity;
import com.supervision.livraisons.dto.SyncUpdateStatusRequest;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.network.ApiService;
import com.supervision.livraisons.util.Constants;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class SyncRepository {

    public static final String LOCAL_STATUS_PENDING = "pending";
    public static final String LOCAL_STATUS_DONE = "done";
    public static final String LOCAL_STATUS_CLIENT_NOT_FOUND = "client_not_found";

    private final DeliveryDao deliveryDao;
    private final MessageDao messageDao;
    private final ApiService apiService;
    private final ExecutorService ioExecutor;
    private final Handler mainHandler;

    @Inject
    public SyncRepository(DeliveryDao deliveryDao, MessageDao messageDao, ApiService apiService) {
        this.deliveryDao = deliveryDao;
        this.messageDao = messageDao;
        this.apiService = apiService;
        this.ioExecutor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public LiveData<List<Delivery>> observeDeliveries() {
        return Transformations.map(deliveryDao.getAllDeliveries(), this::mapEntitiesToDeliveries);
    }

    public void syncDailyDeliveries(String driverId, Double lat, Double lng, Runnable onDone) {
        if (TextUtils.isEmpty(driverId)) {
            if (onDone != null) {
                onDone.run();
            }
            return;
        }

        ioExecutor.execute(() -> {
            try {
                Response<List<Delivery>> response = apiService
                        .getDailyDeliveries(driverId, LocalDate.now().toString(), lat, lng)
                        .execute();

                if (response.isSuccessful() && response.body() != null) {
                    List<DeliveryEntity> entities = mapDeliveriesToEntities(response.body());
                    deliveryDao.clearAllDeliveries();
                    deliveryDao.insertDeliveries(entities);
                }

                syncEmergencyMessagesBlocking();
            } catch (IOException ignored) {
                // Keep local cache untouched when offline.
            } finally {
                if (onDone != null) {
                    mainHandler.post(onDone);
                }
            }
        });
    }

    public void syncDailyDeliveriesBlocking(String driverId) {
        if (TextUtils.isEmpty(driverId)) {
            return;
        }

        try {
            // Background worker has no location — backend returns schedule-ordered list.
            Response<List<Delivery>> response = apiService
                    .getDailyDeliveries(driverId, LocalDate.now().toString(), null, null)
                    .execute();

            if (response.isSuccessful() && response.body() != null) {
                List<DeliveryEntity> entities = mapDeliveriesToEntities(response.body());
                deliveryDao.clearAllDeliveries();
                deliveryDao.insertDeliveries(entities);
            }

            syncEmergencyMessagesBlocking();
        } catch (IOException ignored) {
            // Keep local cache untouched when offline.
        }
    }

    private void syncEmergencyMessagesBlocking() throws IOException {
        Response<List<Message>> response = apiService.getEmergencyMessages().execute();
        if (response.isSuccessful() && response.body() != null) {
            List<MessageEntity> entities = mapMessagesToEntities(response.body());
            messageDao.clearAllMessages();
            messageDao.insertMessages(entities);
        }
    }

    public void updateStatusLocalFirst(String deliveryId, String incomingStatus, String notes) {
        if (TextUtils.isEmpty(deliveryId)) {
            return;
        }

        final String localStatus = toLocalStatus(incomingStatus);
        final String safeNotes = notes == null ? "" : notes;
        ioExecutor.execute(() -> deliveryDao.updateDeliveryStatusAndNotes(deliveryId, localStatus, safeNotes));

        SyncUpdateStatusRequest request = new SyncUpdateStatusRequest(
                deliveryId,
                toRemoteStatus(localStatus),
                OffsetDateTime.now().toString(),
                safeNotes
        );

        apiService.updateStatusViaSync(request).enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(Call<Delivery> call, Response<Delivery> response) {
                // Local-first flow: no rollback needed.
            }

            @Override
            public void onFailure(Call<Delivery> call, Throwable t) {
                // Silent failure: next sync will reconcile state.
            }
        });
    }

    public void emergencySearch(String query, Callback<List<Delivery>> callback) {
        apiService.emergencyClientSearch(query).enqueue(callback);
    }

    private List<DeliveryEntity> mapDeliveriesToEntities(List<Delivery> deliveries) {
        if (deliveries == null || deliveries.isEmpty()) {
            return Collections.emptyList();
        }

        List<DeliveryEntity> entities = new ArrayList<>();
        for (Delivery delivery : deliveries) {
            if (delivery == null || TextUtils.isEmpty(delivery.getId())) {
                continue;
            }

            entities.add(new DeliveryEntity(
                    delivery.getId(),
                    delivery.getClientName(),
                    delivery.getAddress(),
                    delivery.getClientPhone(),
                    delivery.getNotes(),
                    toLocalStatus(delivery.getStatus())
            ));
        }
        return entities;
    }

    private List<MessageEntity> mapMessagesToEntities(List<Message> messages) {
        if (messages == null || messages.isEmpty()) {
            return Collections.emptyList();
        }

        List<MessageEntity> entities = new ArrayList<>();
        for (Message message : messages) {
            if (message == null || TextUtils.isEmpty(message.getId())) {
                continue;
            }
            String ts = message.getTimestamp() != null ? message.getTimestamp().toString() : null;
            entities.add(new MessageEntity(
                    message.getId(),
                    message.getSenderId(),
                    message.getContent(),
                    ts
            ));
        }
        return entities;
    }

    private List<Delivery> mapEntitiesToDeliveries(List<DeliveryEntity> entities) {
        List<Delivery> deliveries = new ArrayList<>();
        if (entities == null) {
            return deliveries;
        }

        for (DeliveryEntity entity : entities) {
            Delivery delivery = new Delivery();
            delivery.setId(entity.getId());
            delivery.setClientName(entity.getClientName());
            delivery.setAddress(entity.getAddress());
            delivery.setClientPhone(entity.getPhone());
            delivery.setNotes(entity.getNotes());
            delivery.setStatus(toUiStatus(entity.getStatus()));
            deliveries.add(delivery);
        }
        return deliveries;
    }

    private String toLocalStatus(String status) {
        if (TextUtils.isEmpty(status)) {
            return LOCAL_STATUS_PENDING;
        }

        String value = status.trim().toLowerCase();
        if ("livre".equals(value) || LOCAL_STATUS_DONE.equals(value) || "done".equals(value)) {
            return LOCAL_STATUS_DONE;
        }
        if ("echoue".equals(value) || LOCAL_STATUS_CLIENT_NOT_FOUND.equals(value) || "client_not_found".equals(value)) {
            return LOCAL_STATUS_CLIENT_NOT_FOUND;
        }
        return LOCAL_STATUS_PENDING;
    }

    private String toRemoteStatus(String localStatus) {
        if (LOCAL_STATUS_DONE.equals(localStatus)) {
            return Constants.STATUS_LIVRE;
        }
        if (LOCAL_STATUS_CLIENT_NOT_FOUND.equals(localStatus)) {
            return Constants.STATUS_ECHOUE;
        }
        return Constants.STATUS_EN_COURS;
    }

    private String toUiStatus(String localStatus) {
        if (LOCAL_STATUS_DONE.equals(localStatus)) {
            return Constants.STATUS_LIVRE;
        }
        if (LOCAL_STATUS_CLIENT_NOT_FOUND.equals(localStatus)) {
            return Constants.STATUS_ECHOUE;
        }
        return Constants.STATUS_EN_ATTENTE;
    }
}
