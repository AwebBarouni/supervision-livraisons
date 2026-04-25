package com.supervision.livraisons.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("deliveries")
public class Delivery {

    @Id
    private String id;
    private String clientName;
    private String clientPhone;
    private String address;
    private Double lat;
    private Double lng;
    private String orderDetails;
    private String notes;
    private String status;
    private String failureReason;
    private String assignedLivreurId;
    private Date scheduledTime;
    private Date updatedAt;
    private Date createdAt;

    public Delivery() {
    }

    public Delivery(String id, String clientName, String clientPhone, String address, Double lat, Double lng, String orderDetails,
                    String notes, String status, String failureReason, String assignedLivreurId, Date scheduledTime,
                    Date updatedAt, Date createdAt) {
        this.id = id;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.orderDetails = orderDetails;
        this.notes = notes;
        this.status = status;
        this.failureReason = failureReason;
        this.assignedLivreurId = assignedLivreurId;
        this.scheduledTime = scheduledTime;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
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

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getAssignedLivreurId() {
        return assignedLivreurId;
    }

    public void setAssignedLivreurId(String assignedLivreurId) {
        this.assignedLivreurId = assignedLivreurId;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
