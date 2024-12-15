package com.example.hikemate.ui.feed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hikemate.R;
import com.example.hikemate.model.LikeResponse;
import com.example.hikemate.model.Post;
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.PostsApi;
import com.example.hikemate.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private Context context;
    private List<Post> posts;
    private boolean isLiked;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);

        holder.userName.setText(post.getUser_name());
        holder.postTitle.setText(post.getTitle());
        holder.postContent.setText(post.getContent());
        holder.postLikes.setText(post.getLikes() + " Likes");

        // Load images using Glide
        Glide.with(context)
                .load("http://10.15.41.122:3001/" + post.getUser_profile())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.userProfileImage);

        Glide.with(context)
                .load("http://10.15.41.122:3001/" + post.getFile_path())
                .placeholder(R.drawable.ic_gallery)
                .into(holder.postImage);

        holder.likeButton.setImageResource(post.getLiked() ? R.drawable.ic_likefill : R.drawable.ic_like);

        holder.likeButton.setOnClickListener(v -> {
            boolean isLiked = post.getLiked();
            post.setLiked(!isLiked);

            holder.likeButton.setImageResource(post.getLiked() ? R.drawable.ic_likefill : R.drawable.ic_like);

            likePost(post, holder);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, postImage;
        TextView userName, postTitle, postContent, postLikes;
        ImageButton likeButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            postImage = itemView.findViewById(R.id.post_image);
            userName = itemView.findViewById(R.id.user_name);
            postTitle = itemView.findViewById(R.id.post_title);
            postContent = itemView.findViewById(R.id.post_content);
            postLikes = itemView.findViewById(R.id.post_likes);
            likeButton = itemView.findViewById(R.id.like_button);
        }
    }

    private void likePost(Post post, PostViewHolder holder) {
        PostsApi postsApi = RetrofitClient.getInstance().create(PostsApi.class);
        String accessToken = "Bearer " + context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                .getString("accessToken", "");
        Log.d("PostLike", "Successfully get sharedprefs token");

        Call<LikeResponse> call = postsApi.likePost(accessToken, post.getId());
        Log.d("PostLike", call.toString());
        call.enqueue(new Callback<LikeResponse>() {
            @Override
            public void onResponse(Call<LikeResponse> call, Response<LikeResponse> response) {
                Log.d("PostLike", call.toString() + " Response: " + response);
                if (response.isSuccessful()) {
                    // Increment the like count locally

                    if (post.getLiked() == true) {
                        post.setLikes(post.getLikes() + 1);
                        post.setLiked(true);
                    }
                    else {
                        post.setLikes(post.getLikes() - 1);
                        post.setLiked(false);
                    }

                    holder.postLikes.setText(post.getLikes() + " Likes");
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LikeResponse> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}