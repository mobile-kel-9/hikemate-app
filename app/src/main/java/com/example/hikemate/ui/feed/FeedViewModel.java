package com.example.hikemate.ui.feed;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hikemate.model.Post;
import com.example.hikemate.model.PostListResponse;
import com.example.hikemate.network.PostsApi;
import com.example.hikemate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedViewModel extends ViewModel {
    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchPosts(String token) {
        PostsApi api = RetrofitClient.getInstance().create(PostsApi.class);
        api.getAllPosts("Bearer " + token).enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    posts.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to fetch posts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<PostListResponse> call, Throwable t) {
                errorMessage.setValue("Error fetching posts: " + t.getMessage());
            }
        });
    }
}
