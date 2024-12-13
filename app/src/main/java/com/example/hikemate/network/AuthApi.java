package com.example.hikemate.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.hikemate.model.HikeSpot;
import com.example.hikemate.model.HikeSpotResponse;
import com.example.hikemate.model.LoginRequest;
import com.example.hikemate.model.RegisterRequest;
import com.example.hikemate.model.LoginResponse;
import com.example.hikemate.model.RegisterResponse;
//import com.example.hikemate.model.SOSRequest;
import com.example.hikemate.model.UserResponse;

import java.util.List;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("me")
    Call<UserResponse> validateToken(@Header("Authorization") String token);

    @GET("hikespots")
    Call<HikeSpotResponse> getHikeSpots(@Header("Authorization") String token);

//    @POST("sos/{chat_id}")
//    Call<Void> sendSOS(@Path("chat_id") String chatId, @Body SOSRequest sosRequest);
}