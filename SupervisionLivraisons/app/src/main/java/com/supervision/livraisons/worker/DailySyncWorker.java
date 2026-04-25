package com.supervision.livraisons.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.supervision.livraisons.repository.SyncRepository;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class DailySyncWorker extends Worker {

    public static final String KEY_DRIVER_ID = "driver_id";

    private final SyncRepository syncRepository;

    @AssistedInject
    public DailySyncWorker(@Assisted @NonNull Context context,
                           @Assisted @NonNull WorkerParameters workerParams,
                           SyncRepository syncRepository) {
        super(context, workerParams);
        this.syncRepository = syncRepository;
    }

    @NonNull
    @Override
    public Result doWork() {
        String driverId = getInputData().getString(KEY_DRIVER_ID);
        syncRepository.syncDailyDeliveriesBlocking(driverId);
        return Result.success();
    }
}
