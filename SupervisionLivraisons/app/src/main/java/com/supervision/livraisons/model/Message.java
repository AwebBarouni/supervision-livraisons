package com.supervision.livraisons.model;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName(value = "id", alternate = {"_id"})
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;

    @SerializedName(value = "isEmergency", alternate = {"emergency"})
    private boolean isEmergency;

    @SerializedName(value = "isRead", alternate = {"read"})
    private boolean isRead;

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
