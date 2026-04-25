package com.supervision.livraisons.model;

public class DeliveryStats {

    private int total;
    private int delivered;
    private int inProgress;
    private int failed;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }
}
