package com.supervision.livraisons;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import com.supervision.livraisons.network.EmergencyWebSocketManager;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class SupervisionLivraisonsApp extends android.app.Application implements Configuration.Provider {

    @Inject HiltWorkerFactory workerFactory;
    @Inject EmergencyWebSocketManager emergencyWebSocketManager;

    @Override
    public void onCreate() {
        super.onCreate();
        connectWebSocketIfLivreur();
    }

    public void connectWebSocketIfLivreur() {
        String role = SessionManager.getUserRole(this);
        String token = SessionManager.getToken(this);
        if (Constants.ROLE_LIVREUR.equals(role) && !TextUtils.isEmpty(token)) {
            emergencyWebSocketManager.connect();
        }
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }
}
