package com.supervision.livraisons.network;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.supervision.livraisons.data.local.MessageDao;
import com.supervision.livraisons.data.local.MessageEntity;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.util.Constants;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@Singleton
public class EmergencyWebSocketManager {

    private static final String WS_URL = "ws://192.168.1.40:8082/ws/emergency";
    private static final String CHANNEL_ID = "emergency_messages";
    private static final long RECONNECT_DELAY_MS = 5_000;

    private final OkHttpClient okHttpClient;
    private final MessageDao messageDao;
    private final Context context;
    private final Gson gson = new Gson();
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final Handler reconnectHandler = new Handler(Looper.getMainLooper());

    private WebSocket webSocket;
    private boolean intentionalClose = false;

    @Inject
    public EmergencyWebSocketManager(OkHttpClient okHttpClient,
            MessageDao messageDao,
            @ApplicationContext Context context) {
        this.okHttpClient = okHttpClient;
        this.messageDao = messageDao;
        this.context = context;
        createNotificationChannel();
    }

    public void connect() {
        intentionalClose = false;
        openConnection();
    }

    public void disconnect() {
        intentionalClose = true;
        reconnectHandler.removeCallbacksAndMessages(null);
        if (webSocket != null) {
            webSocket.close(1000, null);
            webSocket = null;
        }
    }

    private void openConnection() {
        Request request = new Request.Builder()
                .url(WS_URL)
                .build();

        webSocket = okHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(@NonNull WebSocket ws, @NonNull String text) {
                handleIncomingMessage(text);
            }

            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, Response response) {
                scheduleReconnect();
            }

            @Override
            public void onClosed(@NonNull WebSocket ws, int code, @NonNull String reason) {
                if (!intentionalClose) {
                    scheduleReconnect();
                }
            }
        });
    }

    private void handleIncomingMessage(String json) {
        Message message = gson.fromJson(json, Message.class);
        if (message == null || TextUtils.isEmpty(message.getId()))
            return;

        dbExecutor.execute(() -> {
            MessageEntity entity = new MessageEntity(
                    message.getId(),
                    message.getSenderId(),
                    message.getContent(),
                    message.getTimestamp());
            messageDao.insertMessages(Collections.singletonList(entity));
        });

        showNotification(message.getContent());
    }

    private void scheduleReconnect() {
        if (intentionalClose)
            return;
        reconnectHandler.postDelayed(this::openConnection, RECONNECT_DELAY_MS);
    }

    private void showNotification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Message d'urgence")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Messages d'urgence",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Alertes d'urgence de la supervision");
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
