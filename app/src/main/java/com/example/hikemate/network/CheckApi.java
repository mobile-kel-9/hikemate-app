package com.example.hikemate.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CheckApi {
    @GET("")
    Call<String> checkApi();
}

