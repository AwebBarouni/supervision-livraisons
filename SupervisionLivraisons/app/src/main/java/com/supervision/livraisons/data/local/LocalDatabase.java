package com.supervision.livraisons.data.local;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {DeliveryEntity.class, MessageEntity.class}, version = 2, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract DeliveryDao deliveryDao();
    public abstract MessageDao messageDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `emergency_messages` " +
                "(`id` TEXT NOT NULL, `senderId` TEXT, `content` TEXT, `timestamp` TEXT, " +
                "PRIMARY KEY(`id`))"
            );
        }
    };
}
