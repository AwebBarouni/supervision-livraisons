package com.supervision.livraisons.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DeliveryEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract DeliveryDao deliveryDao();
}
