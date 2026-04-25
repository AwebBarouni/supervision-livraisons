package com.supervision.livraisons.di;

import android.content.Context;

import androidx.room.Room;

import com.supervision.livraisons.data.local.DeliveryDao;
import com.supervision.livraisons.data.local.LocalDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class DatabaseModule {

    private DatabaseModule() {
    }

    @Provides
    @Singleton
    public static LocalDatabase provideLocalDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, LocalDatabase.class, "livraisons_local_db").build();
    }

    @Provides
    @Singleton
    public static DeliveryDao provideDeliveryDao(LocalDatabase database) {
        return database.deliveryDao();
    }
}
