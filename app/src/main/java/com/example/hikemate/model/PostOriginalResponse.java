package com.example.hikemate.model;

public class PostOriginalResponse {
    private boolean success;
    private int status;
    private String message;
    private PostOriginal data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public PostOriginal getData() {
        return data;
    }
}