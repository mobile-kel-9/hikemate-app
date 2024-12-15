package com.example.hikemate.utils;

public interface GetMeCallback {
    void onSuccess(String name, String email, String country, String image_path);
    void onError(String error);
}
