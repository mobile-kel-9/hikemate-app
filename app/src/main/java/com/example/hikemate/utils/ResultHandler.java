package com.example.hikemate.utils;

import com.example.hikemate.model.HikeSpotProxResponse;

import java.util.List;

public interface ResultHandler {
    void onResult(String chatId, String Place);
    void onError(Throwable throwable);
}
