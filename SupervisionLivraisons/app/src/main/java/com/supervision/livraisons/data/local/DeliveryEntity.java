package com.supervision.livraisons.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "deliveries")
public class DeliveryEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String clientName;
    private String address;
    private String phone;
    private String notes;
    private String status;
    private Double lat;
    private Double lng;
    private String scheduledTime;

    public DeliveryEntity(@NonNull String id, String clientName, String address, String phone,
            String notes, String status, Double lat, Double lng, String scheduledTime) {
        this.id = id;
        this.clientName = clientName;
        this.address = address;
        this.phone = phone;
        this.notes = notes;
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.scheduledTime = scheduledTime;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
}