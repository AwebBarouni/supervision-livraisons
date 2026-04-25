package com.supervision.livraisons.dto;

public class SendMessageRequest {

    private String receiverId;
    private String content;
    private boolean isEmergency;

    public SendMessageRequest(String receiverId, String content, boolean isEmergency) {
        this.receiverId = receiverId;
        this.content = content;
        this.isEmergency = isEmergency;
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

    public boolean isEmergency() {
        return isEmergency;
    }

    public void setEmergency(boolean emergency) {
        isEmergency = emergency;
    }
}
