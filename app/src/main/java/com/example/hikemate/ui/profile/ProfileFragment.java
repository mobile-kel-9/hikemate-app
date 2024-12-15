package com.example.hikemate.ui.profile;

import static android.content.Context.MODE_PRIVATE;
import static androidx.databinding.adapters.TextViewBindingAdapter.setText;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.hikemate.LoginActivity;
import com.example.hikemate.ProfileEditActivity;
import com.example.hikemate.R;
import com.example.hikemate.databinding.FragmentProfileBinding;
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.RetrofitClient;
import com.example.hikemate.model.MeResponse;
import com.example.hikemate.model.UserProfile;
import com.example.hikemate.ui.home.HomeViewModel;
import com.example.hikemate.utils.GetMeCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserProfile userProfile;
    private HomeViewModel homeViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
        String token = sharedPreferences.getString("accessToken", null);

        if (token != null) {
            getMe(token, new GetMeCallback() {
                @Override
                public void onSuccess(String name, String email, String country, String image_path) {
                    binding.tvUserName.setText(name);
                    binding.tvUserEmail.setText(email);
                    Glide.with(ProfileFragment.this).load(userProfile.getImagePath()).into(binding.ivProfilePicture);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("ProfileFragment", errorMessage);
                }
            });
        } else {
            Log.e("ProfileFragment", "Token is null");
        }
        binding.btnLogout.setOnClickListener(v -> logout());
        binding.btnEditProfile.setOnClickListener(v -> editProfile());
    }

    private void getMe(String token, GetMeCallback callback) {
        AuthApi authService = RetrofitClient.getAuthApi();
        Call<MeResponse> call = authService.validateToken("Bearer " + token);
        call.enqueue(new Callback<MeResponse>() {
            @Override
            public void onResponse(Call<MeResponse> call, Response<MeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userProfile = response.body().getData();
                    Log.d("GetMe", "User  ID: " + userProfile.getId());
                    Log.d("GetMe", "Name: " + userProfile.getName());
                    Log.d("GetMe", "Email: " + userProfile.getEmail());
                    Log.d("GetMe", "Country: " + userProfile.getCountry());
                    Log.d("GetMe", "Birth Date: " + userProfile.getBirthDate());
                    Log.d("GetMe", "Role: " + userProfile.getRole());
                    Log.d("GetMe", "Image Path: " + userProfile.getImagePath());

                    callback.onSuccess(userProfile.getName(), userProfile.getEmail(), userProfile.getCountry(), userProfile.getImagePath());
                } else {
                    Log.e("GetMe", "Response error: " + response.code());
                    callback.onError("Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call <MeResponse> call, Throwable t) {
                callback.onError("API call failed: " + t.getMessage());
            }
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("accessToken");
        editor.apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);

        requireActivity().finish();
    }

    private void editProfile() {
        Log.d("EditProfileIntent", "Masuk");
        Intent intent = new Intent(getContext(), ProfileEditActivity.class);

        intent.putExtra("name", getDataFromSharedPreferences("name"));
        intent.putExtra("country", getDataFromSharedPreferences("country"));
        intent.putExtra("birth_date", getDataFromSharedPreferences("birth_date"));
        intent.putExtra("image_path", getDataFromSharedPreferences("image_path"));
        intent.putExtra("access_token", getDataFromSharedPreferences("accessToken"));

        startActivity(intent);
    }

    private String getDataFromSharedPreferences(String key) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}