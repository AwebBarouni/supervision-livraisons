package com.supervision.livraisons.worker;

import android.content.Context;
import android.text.TextUtils;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public final class SyncWorkScheduler {

    private SyncWorkScheduler() {
    }

    public static void enqueueDailySync(Context context, String driverId) {
        if (TextUtils.isEmpty(driverId)) {
            return;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data input = new Data.Builder()
                .putString(DailySyncWorker.KEY_DRIVER_ID, driverId)
                .build();

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(DailySyncWorker.class, 1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInputData(input)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("daily_sync_" + driverId, ExistingPeriodicWorkPolicy.UPDATE, request);
    }
}
