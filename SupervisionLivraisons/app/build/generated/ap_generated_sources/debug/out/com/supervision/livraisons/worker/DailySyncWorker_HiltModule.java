package com.supervision.livraisons.worker;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = DailySyncWorker.class
)
public interface DailySyncWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.supervision.livraisons.worker.DailySyncWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(DailySyncWorker_AssistedFactory factory);
}
