package com.example.hikemate.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.hikemate.model.HikeSpotProxResponse;
import com.example.hikemate.model.HikeSpotResponse;
import com.example.hikemate.model.SOSRequest;
import com.example.hikemate.model.SOSResponse;

public interface HikeSpotApi {
    @GET("hikespots")
    Call<HikeSpotResponse> getHikeSpots(@Header("Authorization") String token);

    @GET("hikespots/{lat}/{long}")
    Call<HikeSpotProxResponse> getHikeSpotsByLocation(
            @Path("lat") double latitude,
            @Path("long") double longitude,
            @Header("Authorization") String token
    );

    @POST("sos/{chat_id}")
    Call<SOSResponse> sendSOS(
            @Path("chat_id") String chatId,
            @Header("Authorization") String token,
            @Body SOSRequest sosRequest
    );
}
