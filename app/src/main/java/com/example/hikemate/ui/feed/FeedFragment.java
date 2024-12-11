package com.example.hikemate.ui.feed;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hikemate.PostCreationActivity;
import com.example.hikemate.R;
import com.example.hikemate.databinding.FragmentFeedBinding;
import com.example.hikemate.model.Post;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private FragmentFeedBinding binding;
    private FeedViewModel feedViewModel;
    private PostAdapter postAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        // Setup RecyclerView
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), new ArrayList<>());
        binding.recyclerViewPosts.setAdapter(postAdapter);

        binding.fabRefresh.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostCreationActivity.class);
            startActivity(intent);
        });
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshPosts);

        // Observe posts
        feedViewModel.getPosts().observe(getViewLifecycleOwner(), this::updatePosts);
        feedViewModel.getErrorMessage().observe(getViewLifecycleOwner(), this::showError);

        // Trigger data fetch
        feedViewModel.fetchPosts(getAccessTokenFromSharedPreferences()); // Replace with actual token

        return binding.getRoot();
    }

    private void updatePosts(List<Post> posts) {
        postAdapter.setPosts(posts);
    }

    private void refreshPosts() {
        // Display the refresh indicator
        binding.swipeRefreshLayout.setRefreshing(true);

        // Simulate an API fetch
        feedViewModel.fetchPosts(getAccessTokenFromSharedPreferences());

        // When data is fetched, turn off the refresh indicator
        feedViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            updatePosts(posts);
            binding.swipeRefreshLayout.setRefreshing(false); // Hide the refresh indicator
        });
    }

    private void showError(String message) {
        binding.textViewError.setText(message);
        binding.textViewError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }
}
