package com.supervision.livraisons.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("messages")
public class Message {

    @Id
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    @JsonProperty("isEmergency")
    private boolean isEmergency;
    @JsonProperty("isRead")
    private boolean isRead;
    private Date timestamp;

    public Message() {
    }

    public Message(String id, String senderId, String receiverId, String content, boolean isEmergency, boolean isRead,
                   Date timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.isEmergency = isEmergency;
        this.isRead = isRead;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonProperty("isEmergency")
    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    @JsonProperty("isRead")
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
