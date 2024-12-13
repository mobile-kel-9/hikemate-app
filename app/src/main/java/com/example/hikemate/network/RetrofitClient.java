package com.example.hikemate.network;

import android.util.Log;

import com.example.hikemate.model.LoginRequest;
import com.example.hikemate.model.LoginResponse;
import com.example.hikemate.model.Mountain;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.15.41.122:3001/api/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void testLogin() {
        AuthApi api = getInstance().create(AuthApi.class);
        LoginRequest loginRequest = new LoginRequest("newuser@gmail.com", "password");

        api.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse.isSuccess()) {
                        String accessToken = loginResponse.getData().getAccessToken();
                        Log.d("RetrofitClient", "Login successful! Access Token: " + accessToken);
                    } else {
                        Log.e("RetrofitClient", "Login failed. Message: " + loginResponse.getMessage());
                    }
                } else {
                    Log.e("RetrofitClient", "Login failed. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("RetrofitClient", "Login request failed. Error: " + t.getMessage());
            }
        });
    }

    public interface MountainService {
        @GET("api/Hikespots/HikespotsController_getDestinations")
        Call<List<Mountain>> getDestinations(@Header("Authorization") String token);
    }

}

