package com.example.hikemate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.hikemate.databinding.PageEditprofileBinding;
import com.bumptech.glide.Glide;
import com.example.hikemate.model.UpdateUserRequest;
import com.example.hikemate.model.UpdateUserResponse;
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.RetrofitClient;
//
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {
    private TextView nameInputHint, dobInputHint, regionInputHint;
    private Button submitButton;
    private ImageView profilePicture;

//    private PageEditprofileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = PageEditprofileBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        setContentView(R.layout.page_editprofile);

        Log.d("PEA",
                getIntent().getStringExtra("name") + " " +
                     getIntent().getStringExtra("country") + " " +
                     getIntent().getStringExtra("birth_date") + " " +
                     getIntent().getStringExtra("image_path") + " " +
                     getIntent().getStringExtra("access_token")
        );

        nameInputHint = findViewById(R.id.name_input);
        dobInputHint = findViewById(R.id.dob_input);
        regionInputHint = findViewById(R.id.region_input);
        submitButton = findViewById(R.id.submit_button);
        profilePicture = findViewById(R.id.profile_picture);

        String imagePath = getIntent().getStringExtra("image_path");

        if (imagePath != null && !imagePath.isEmpty()) {
            // Use Glide to load the image
            Glide.with(this)
                    .load(imagePath) // URL or URI of the image
                    .placeholder(R.drawable.ic_profile_placeholder) // Placeholder image
                    .error(R.drawable.ic_profile_placeholder) // Error image if loading fails
                    .into(profilePicture); // Target ImageView
        } else {
            Log.e("ProfileEditActivity", "No image path provided!");
        }

        submitButton.setOnClickListener(view -> submitUpdate());

        String rawDate = getIntent().getStringExtra("birth_date");
        String formattedDate = rawDate != null && rawDate.length() >= 10 ? rawDate.substring(0, 10) : "";

        nameInputHint.setText(getIntent().getStringExtra("name"));
        dobInputHint.setText(formattedDate);
        regionInputHint.setText(getIntent().getStringExtra("country"));
//        binding.nameInput.setText(getIntent().getStringExtra("name"));
//
//        String rawDate = getIntent().getStringExtra("birth_date");
//        String formattedDate = rawDate != null && rawDate.length() >= 10 ? rawDate.substring(0, 10) : "";
//        binding.dobInput.setText(formattedDate);
//
//        binding.regionInput.setText(getIntent().getStringExtra("country"));

        // Set click listener for the submit button
//        binding.submitButton.setOnClickListener(view -> submitUpdate());
    }

    private void setMeDataToSharedPreferences(String key, String data) {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, data);
        editor.apply();
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
//
    private void submitUpdate() {
//        String name = binding.nameInput.getText().toString();
        String name = nameInputHint.getText().toString();
        if (name.isEmpty()) {
            name = getIntent().getStringExtra("name");
        }

//        String dob = binding.dobInput.getText().toString();
        String dob = dobInputHint.getText().toString();
        if (dob.isEmpty()) {
            dob = getIntent().getStringExtra("birth_date").substring(0, 10); // Default to the formatted value
        }

        if (!isValidDate(dob)) {
            Toast.makeText(this, "Invalid format. Please use YYYY-MM-DD.", Toast.LENGTH_SHORT).show();
            return;
        }

//        String region = binding.regionInput.getText().toString();
        String region = regionInputHint.getText().toString();
        if (region.isEmpty()) {
            region = getIntent().getStringExtra("country");
        }

        AuthApi api = RetrofitClient.getInstance().create(AuthApi.class);
        UpdateUserRequest request = new UpdateUserRequest(name, dob, region);
        Call<UpdateUserResponse> call = api.updateUser("Bearer " + getIntent().getStringExtra("access_token"), request);

        String finalName = name;
        String finalDob = dob;
        String finalRegion = region;

        call.enqueue(new Callback<UpdateUserResponse>() {
            @Override
            public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                if (response.isSuccessful()) {
                    setMeDataToSharedPreferences("name", finalName);
                    setMeDataToSharedPreferences("birth_date", finalDob);
                    setMeDataToSharedPreferences("country", finalRegion);

                    Toast.makeText(ProfileEditActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.d("onResponse", response.toString());
                    Toast.makeText(ProfileEditActivity.this, "Update failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                Toast.makeText(ProfileEditActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
