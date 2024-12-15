package com.example.hikemate.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.hikemate.PostCreationActivity;
import com.example.hikemate.R;
import com.example.hikemate.databinding.FragmentHomeBinding;
import com.example.hikemate.model.Post;
import com.example.hikemate.ui.feed.FeedViewModel;
import com.example.hikemate.ui.feed.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private FeedViewModel feedViewModel;
    private PostAdapter postAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        TextView placeTextView = view.findViewById(R.id.mountain_name);
        TextView heightTextView = view.findViewById(R.id.current_height);
        TextView nameTextView = view.findViewById(R.id.current_username);
        TextView countryTextView = view.findViewById(R.id.user_country);

        feedViewModel = new ViewModelProvider(this).get(FeedViewModel.class);

        // Setup RecyclerView
        binding.recyclerViewPostsHome.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), new ArrayList<>());
        binding.recyclerViewPostsHome.setAdapter(postAdapter);
        
//        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshPosts);

        homeViewModel.getPlace().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String place) {
                placeTextView.setText(place);
            }
        });

        homeViewModel.getHeight().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float height) {
                heightTextView.setText(String.valueOf(height) + " mdpl");
            }
        });
        homeViewModel.getName().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String name) {
                nameTextView.setText("Hi, " + name);
            }
        });

        homeViewModel.getCountry().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String country) {
                countryTextView.setText(country);
            }
        });
        
        feedViewModel.getPosts().observe(getViewLifecycleOwner(), this::updatePosts);

        feedViewModel.fetchPosts(getAccessTokenFromSharedPreferences());

        return view;
    }

    private void updatePosts(List<Post> posts) {
        postAdapter.setPosts(posts);
    }

    private void refreshPosts() {
//        binding.swipeRefreshLayout.setRefreshing(true);

        feedViewModel.fetchPosts(getAccessTokenFromSharedPreferences());

        feedViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            updatePosts(posts);
//            binding.swipeRefreshLayout.setRefreshing(false); // Hide the refresh indicator
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}