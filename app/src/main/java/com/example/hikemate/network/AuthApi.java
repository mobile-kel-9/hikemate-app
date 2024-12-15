package com.example.hikemate.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

import com.example.hikemate.model.LoginRequest;
import com.example.hikemate.model.MeResponse;
import com.example.hikemate.model.RegisterRequest;
import com.example.hikemate.model.LoginResponse;
import com.example.hikemate.model.RegisterResponse;
import com.example.hikemate.model.UpdateUserRequest;
import com.example.hikemate.model.UpdateUserResponse;
import com.example.hikemate.model.UserResponse;

public interface AuthApi {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);

    @GET("auth/me")
    Call<MeResponse> validateToken(@Header("Authorization") String token);

    @PATCH("auth/patch")
    Call<UpdateUserResponse> updateUser(
            @Header("Authorization") String token,
            @Body UpdateUserRequest updateUserRequest
    );
}