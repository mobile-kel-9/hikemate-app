package com.example.hikemate.utils;

import java.util.List;
import com.example.hikemate.model.HikeSpotProxResponse;

public interface HikeSpotCallback {
    void onHikeSpotReceived(List<HikeSpotProxResponse.HikeSpot> hikeSpots);
    void onFailure(Throwable throwable);
}
