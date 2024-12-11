package com.example.hikemate.network;

import android.util.Log;

import com.example.hikemate.model.LoginRequest;
import com.example.hikemate.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    public static HikeSpotApi getHikeSpotApi() {
        return getInstance().create(HikeSpotApi.class);
    }
}

