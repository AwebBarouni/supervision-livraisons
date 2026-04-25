package com.supervision.livraisons.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("SELECT * FROM emergency_messages ORDER BY timestamp DESC")
    LiveData<List<MessageEntity>> getAllEmergencyMessages();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<MessageEntity> messages);

    @Query("DELETE FROM emergency_messages")
    void clearAllMessages();
}
