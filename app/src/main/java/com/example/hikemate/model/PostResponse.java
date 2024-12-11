package com.example.hikemate.model;

public class PostResponse {
    private boolean success;
    private int status;
    private String message;
    private Post data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Post getData() {
        return data;
    }
}
