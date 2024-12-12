package com.example.hikemate.model;

import java.util.List;

public class PostListResponse {
    private boolean success;
    private int status;
    private String message;
    private List<Post> data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<Post> getData() {
        return data;
    }
}
