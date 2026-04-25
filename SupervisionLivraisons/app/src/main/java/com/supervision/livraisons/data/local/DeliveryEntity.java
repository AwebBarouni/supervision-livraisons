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

    public DeliveryEntity(@NonNull String id, String clientName, String address, String phone, String notes, String status) {
        this.id = id;
        this.clientName = clientName;
        this.address = address;
        this.phone = phone;
        this.notes = notes;
        this.status = status;
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
}
