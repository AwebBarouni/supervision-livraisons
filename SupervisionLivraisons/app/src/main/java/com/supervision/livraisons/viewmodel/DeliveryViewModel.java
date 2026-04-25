package com.supervision.livraisons.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.supervision.livraisons.R;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.network.ApiService;
import com.supervision.livraisons.repository.SyncRepository;
import com.supervision.livraisons.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class DeliveryViewModel extends AndroidViewModel {

    private final SyncRepository syncRepository;
    private final ApiService apiService;

    private final LiveData<List<Delivery>> deliveries;
    private final MutableLiveData<Delivery> deliveryDetail = new MutableLiveData<>();
    private final MutableLiveData<List<Delivery>> emergencyResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    @Inject
    public DeliveryViewModel(@NonNull Application application,
                             SyncRepository syncRepository,
                             ApiService apiService) {
        super(application);
        this.syncRepository = syncRepository;
        this.apiService = apiService;
        this.deliveries = syncRepository.observeDeliveries();
    }

    public LiveData<List<Delivery>> getDeliveries() {
        return deliveries;
    }

    public LiveData<Delivery> getDeliveryDetail() {
        return deliveryDetail;
    }

    public LiveData<List<Delivery>> getEmergencyResults() {
        return emergencyResults;
    }

    public LiveData<Boolean> getUpdateSuccess() {
        return updateSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadTodayDeliveries(Context context, Double lat, Double lng) {
        isLoading.setValue(true);
        syncRepository.syncDailyDeliveries(SessionManager.getUserId(context), lat, lng,
                () -> isLoading.setValue(false));
    }

    public void loadAllDeliveries(Context context) {
        loadTodayDeliveries(context, null, null);
    }

    public void loadDelivery(String id) {
        isLoading.setValue(true);
        apiService.getDelivery(id).enqueue(new Callback<Delivery>() {
            @Override
            public void onResponse(@NonNull Call<Delivery> call, @NonNull Response<Delivery> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    deliveryDetail.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Delivery> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void updateStatus(String deliveryId, String status) {
        if (deliveryId == null || status == null) {
            return;
        }

        isLoading.setValue(true);
        updateSuccess.setValue(false);
        syncRepository.updateStatusLocalFirst(deliveryId, status);
        isLoading.setValue(false);
        updateSuccess.setValue(true);
    }

    public void emergencySearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            emergencyResults.setValue(new ArrayList<>());
            return;
        }

        isLoading.setValue(true);
        syncRepository.emergencySearch(query.trim(), new Callback<List<Delivery>>() {
            @Override
            public void onResponse(@NonNull Call<List<Delivery>> call, @NonNull Response<List<Delivery>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    emergencyResults.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Delivery>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }
}
