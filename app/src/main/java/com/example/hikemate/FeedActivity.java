package com.example.hikemate;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hikemate.model.Post;
import com.example.hikemate.model.PostListResponse;
import com.example.hikemate.network.PostsApi;
import com.example.hikemate.network.RetrofitClient;
import com.example.hikemate.ui.feed.PostAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FeedActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_feed);

        recyclerView = findViewById(R.id.recyclerViewPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchPosts();
    }

    private void fetchPosts() {
        Retrofit retrofit = RetrofitClient.getInstance();

        PostsApi postsApi = retrofit.create(PostsApi.class);

        Call<PostListResponse> call = postsApi.getAllPosts("Bearer " + getAccessTokenFromSharedPreferences());
        call.enqueue(new Callback<PostListResponse>() {
            @Override
            public void onResponse(Call<PostListResponse> call, Response<PostListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body().getData();
                    postAdapter = new PostAdapter(FeedActivity.this, posts);

                    recyclerView.setAdapter(postAdapter);
                } else {
                    Log.e("FeedActivity", "Error fetching posts");
                    Toast.makeText(FeedActivity.this, "Failed to fetch posts", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostListResponse> call, Throwable t) {
                Log.e("FeedActivity", "Error fetching posts: " + t.getMessage());
                Toast.makeText(FeedActivity.this, "Error fetching posts", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

}
