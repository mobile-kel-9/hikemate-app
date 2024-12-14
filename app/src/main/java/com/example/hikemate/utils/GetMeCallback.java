package com.example.hikemate.utils;

public interface GetMeCallback {
    void onSuccess(String name, String country);
    void onError(String error);
}
