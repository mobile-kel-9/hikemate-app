package com.example.hikemate.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hikemate.R;
import com.example.hikemate.databinding.FragmentHomeBinding;
import com.example.hikemate.ui.feed.FeedFragment;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        TextView placeTextView = view.findViewById(R.id.mountain_name);
        TextView heightTextView = view.findViewById(R.id.current_height);
        TextView nameTextView = view.findViewById(R.id.current_username);
        TextView countryTextView = view.findViewById(R.id.user_country);

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

        loadFeedFragment();

        return binding.getRoot();
    }

    private void loadFeedFragment() {
        Log.d("HomeFragment", "Loading FeedFragment");
        FeedFragment feedFragment = new FeedFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.feed_fragment_container, feedFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}