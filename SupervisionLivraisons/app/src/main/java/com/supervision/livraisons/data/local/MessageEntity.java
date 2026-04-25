package com.supervision.livraisons.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "emergency_messages")
public class MessageEntity {

    @PrimaryKey
    @NonNull
    private final String id;
    private final String senderId;
    private final String content;
    private final String timestamp;

    public MessageEntity(@NonNull String id, String senderId, String content, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
}
