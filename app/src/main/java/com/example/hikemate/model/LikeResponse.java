package com.example.hikemate.model;

public class LikeResponse {
    private boolean success;
    private int status;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}