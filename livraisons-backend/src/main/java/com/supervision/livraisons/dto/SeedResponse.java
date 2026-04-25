package com.supervision.livraisons.dto;

public class SeedResponse {

    private boolean seeded;
    private String message;

    public SeedResponse() {
    }

    public SeedResponse(boolean seeded, String message) {
        this.seeded = seeded;
        this.message = message;
    }

    public boolean isSeeded() {
        return seeded;
    }

    public void setSeeded(boolean seeded) {
        this.seeded = seeded;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
