package com.example.hikemate.model;

public class SOSRequest {
    private String message;

    public SOSRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}