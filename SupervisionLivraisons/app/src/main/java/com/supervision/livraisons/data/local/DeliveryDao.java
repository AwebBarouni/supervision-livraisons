package com.supervision.livraisons.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DeliveryDao {

    @Query("SELECT * FROM deliveries ORDER BY clientName ASC")
    LiveData<List<DeliveryEntity>> getAllDeliveries();

    @Query("UPDATE deliveries SET status = :status WHERE id = :deliveryId")
    void updateDeliveryStatus(String deliveryId, String status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeliveries(List<DeliveryEntity> deliveries);

    @Query("DELETE FROM deliveries")
    void clearAllDeliveries();
}
