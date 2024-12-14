package com.example.hikemate.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.hikemate.R;
import com.example.hikemate.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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
                heightTextView.setText(String.valueOf(height));
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

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}