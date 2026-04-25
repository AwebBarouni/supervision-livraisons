package com.supervision.livraisons.dto;

import java.util.Date;
import java.util.List;

import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.model.User;

public class StartDaySyncResponse {

    private Date syncedAt;
    private User me;
    private List<Delivery> todayDeliveries;
    private List<Message> messages;

    public StartDaySyncResponse() {
    }

    public StartDaySyncResponse(Date syncedAt, User me, List<Delivery> todayDeliveries, List<Message> messages) {
        this.syncedAt = syncedAt;
        this.me = me;
        this.todayDeliveries = todayDeliveries;
        this.messages = messages;
    }

    public Date getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(Date syncedAt) {
        this.syncedAt = syncedAt;
    }

    public User getMe() {
        return me;
    }

    public void setMe(User me) {
        this.me = me;
    }

    public List<Delivery> getTodayDeliveries() {
        return todayDeliveries;
    }

    public void setTodayDeliveries(List<Delivery> todayDeliveries) {
        this.todayDeliveries = todayDeliveries;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
