package com.example.hikemate.services;

import android.util.Log;

import com.example.hikemate.model.SOSRequest;
import com.example.hikemate.model.SOSResponse;
import com.example.hikemate.network.HikeSpotApi;
import com.example.hikemate.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SOSService {

    public void sendSOS(String chatId, String authToken, SOSRequest sosRequest, final SOSCallback callback) {
        HikeSpotApi api = RetrofitClient.getHikeSpotApi();
        Call<SOSResponse> call = api.sendSOS(chatId, authToken, sosRequest);

        call.enqueue(new Callback<SOSResponse>() {
            @Override
            public void onResponse(Call<SOSResponse> call, Response<SOSResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    // Log the full response body for debugging
                    Log.e("SOSService", "Response error: " + response.message() + " - " + response.errorBody());
                    callback.onFailure(new Exception("Failed to send SOS: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<SOSResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    public interface SOSCallback {
        void onSuccess(SOSResponse sosResponse);
        void onFailure(Throwable throwable);
    }
}
