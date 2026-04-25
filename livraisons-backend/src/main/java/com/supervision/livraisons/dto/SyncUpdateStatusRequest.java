package com.supervision.livraisons.dto;

public class SyncUpdateStatusRequest {

    private String deliveryId;
    private String status;
    private String timestamp;

    public SyncUpdateStatusRequest() {
    }

    public SyncUpdateStatusRequest(String deliveryId, String status, String timestamp) {
        this.deliveryId = deliveryId;
        this.status = status;
        this.timestamp = timestamp;
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
}
