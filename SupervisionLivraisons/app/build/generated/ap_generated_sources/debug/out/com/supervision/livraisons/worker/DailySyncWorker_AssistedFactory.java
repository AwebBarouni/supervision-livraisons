package com.supervision.livraisons.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface DailySyncWorker_AssistedFactory extends WorkerAssistedFactory<DailySyncWorker> {
}
