package com.example.hikemate.network;

import com.example.hikemate.model.DeletePostResponse;
import com.example.hikemate.model.LikeResponse;
import com.example.hikemate.model.PostListResponse;
import com.example.hikemate.model.PostOriginalResponse;
import com.example.hikemate.model.PostResponse;
import com.example.hikemate.model.SOSRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PostsApi {
    @GET("posts")
    Call<PostListResponse> getAllPosts(@Header("Authorization") String token);

    @GET("posts/{id}")
    Call<PostResponse> getAllPosts(
            @Header("Authorization") String token,
            @Path("id") String postId
    );

    @Multipart
    @POST("posts")
    Call<PostOriginalResponse> insertPost(
            @Header("Authorization") String token,
            @Part("title") RequestBody title,
            @Part("content") RequestBody content,
            @Part("place") RequestBody place,
            @Part("file") MultipartBody.Part file
    );

    @DELETE("posts/{id}")
    Call<DeletePostResponse> deletePost(@Path("id") String postId);

    @PATCH("posts/like/{id}")
    Call<LikeResponse> likePost(
            @Header("Authorization") String token,
            @Path("id") String postId
    );
}
