package com.supervision.livraisons.dto;

public class SyncUpdateStatusRequest {

    private String deliveryId;
    private String status;
    private String timestamp;
    private String notes;

    public SyncUpdateStatusRequest(String deliveryId, String status, String timestamp, String notes) {
        this.deliveryId = deliveryId;
        this.status = status;
        this.timestamp = timestamp;
        this.notes = notes;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
