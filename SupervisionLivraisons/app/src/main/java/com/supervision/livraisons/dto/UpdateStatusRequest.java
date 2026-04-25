package com.supervision.livraisons.dto;

public class UpdateStatusRequest {

    private String status;
    private String failureReason;
    private String notes;

    public UpdateStatusRequest(String status, String failureReason, String notes) {
        this.status = status;
        this.failureReason = failureReason;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
