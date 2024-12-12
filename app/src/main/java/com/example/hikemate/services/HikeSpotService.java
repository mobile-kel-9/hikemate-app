package com.example.hikemate.services;

import android.content.SharedPreferences;

import com.example.hikemate.model.HikeSpotProxResponse;
import com.example.hikemate.network.HikeSpotApi;
import com.example.hikemate.utils.BarometerUtil;
import com.example.hikemate.utils.HikeSpotCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HikeSpotService {
    private SOSService sosService;
    private HikeSpotApi hikeSpotApi;
    private BarometerUtil barometerUtil;
    private SharedPreferences sharedPreferences;

    public HikeSpotService(HikeSpotApi hikeSpotApi) {
        this.hikeSpotApi = hikeSpotApi;
    }

    public void getHikeSpots(double latitude, double longitude, String accessToken, HikeSpotCallback callback) {
        Call<HikeSpotProxResponse> call = hikeSpotApi.getHikeSpotsByLocation(latitude, longitude, accessToken);

        call.enqueue(new Callback<HikeSpotProxResponse>() {
            @Override
            public void onResponse(Call<HikeSpotProxResponse> call, Response<HikeSpotProxResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HikeSpotProxResponse.HikeSpot> hikeSpots = response.body().getData();
                    if (!hikeSpots.isEmpty()) {
                        callback.onHikeSpotReceived(hikeSpots);
                    } else {
                        callback.onFailure(new Throwable("No hike spots found"));
                    }
                } else {
                    callback.onFailure(new Throwable("Response error: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<HikeSpotProxResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}

//    public HikeSpotService(HikeSpotApi apiService, SOSService sosService, BarometerUtil barometerUtil, SharedPreferences sharedPreferences) {
//        this.apiService = apiService;
//        this.sosService = sosService;
//        this.barometerUtil = barometerUtil;
//        this.sharedPreferences = sharedPreferences;
//    }

//    public void getHikeSpots(double latitude, double longitude, String accessToken, HikeSpotCallback callback) {
//        Call<HikeSpotProxResponse> call = apiService.getHikeSpotsByLocation(latitude, longitude, accessToken);
//
//        call.enqueue(new Callback<HikeSpotProxResponse>() {
//            @Override
//            public void onResponse(Call<HikeSpotProxResponse> call, Response<HikeSpotProxResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<HikeSpotProxResponse.HikeSpot> hikeSpots = response.body().getData();
//                    for (HikeSpotProxResponse.HikeSpot spot : hikeSpots) {
//                        Log.d("ProximityCheck", "Hike Spot: " + spot.getPlace() + " Chat ID: " + spot.getChatId());
//                        callback.onHikeSpotReceived(hikeSpots);
//
//                        float altitude = barometerUtil.getAltitude();
//                        SOSRequest sosRequest = new SOSRequest(
//                                String.valueOf(latitude),
//                                String.valueOf(longitude),
//                                String.valueOf(altitude),
//                                spot.getPlace(),
//                                "Tolong Kami!"
//                        );
//
//                        String chatId = spot.getChatId();
//
//                        sosService.sendSOS(chatId, accessToken, sosRequest, new SOSService.SOSCallback() {
//                            @Override
//                            public void onSuccess(SOSResponse sosResponse) {
//                                Log.d("SOSService", "SOS sent successfully for chat ID: " + chatId);
//                            }
//
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                Log.e("SOSService", "Error sending SOS for chat ID: " + chatId + " - " + throwable.getMessage());
//                            }
//                        });
//                    }
//                } else {
//                    Log.e(TAG, "Response error: " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HikeSpotProxResponse> call, Throwable t) {
//                Log.e(TAG, "API call failed: " + t.getMessage());
//            }
//        });
//    }
//}