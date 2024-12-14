package com.example.hikemate.utils;

import android.util.Log;

import com.example.hikemate.model.HikeSpotProxResponse;

import java.util.List;


public class HikeSpotCallbackImpl implements HikeSpotCallback {
    private ResultHandler resultHandler;
    private String chatId;
    private static final String TAG = HikeSpotCallbackImpl.class.getSimpleName();

    public HikeSpotCallbackImpl(ResultHandler resultHandler) {
        this.resultHandler = resultHandler;
    }
    @Override
    public void onHikeSpotReceived(List<HikeSpotProxResponse.HikeSpot> hikeSpots) {
        String chatId = hikeSpots.get(0).getChatId();
        String place = hikeSpots.get(0).getPlace();
        resultHandler.onResult(chatId, place);
    }

    public String getChatId() {
        return chatId;
    }
    @Override
    public void onFailure(Throwable throwable) {
        Log.e(TAG, "API call failed: " + throwable.getMessage());
        resultHandler.onError(throwable);
    }
}