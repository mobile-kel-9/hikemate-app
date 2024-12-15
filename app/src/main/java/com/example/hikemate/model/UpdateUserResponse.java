package com.example.hikemate.model;

public class UpdateUserResponse {
    private boolean success;
    private int status;
    private String message;
    private UpdateUser data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UpdateUser getData() {
        return data;
    }
}
