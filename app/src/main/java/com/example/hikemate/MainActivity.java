package com.example.hikemate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.hikemate.database.DatabaseHelper;
import com.example.hikemate.databinding.ActivityMainBinding;
import com.example.hikemate.model.HikeSpot;
import com.example.hikemate.model.HikeSpotResponse;
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.RetrofitClient;
import com.example.hikemate.services.LocationService;
import com.example.hikemate.utils.LocationUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private FallDetection fallDetection;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.logout_button);

        fetchAndStoreHikeSpots();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLocation();
        }

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
        fallDetection = new FallDetection(this);
    }

    private void handleLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void fetchAndStoreHikeSpots() {
        AuthApi AuthApi = RetrofitClient.getInstance().create(AuthApi.class);

        String accessToken = "Bearer " + getAccessTokenFromSharedPreferences();
        Call<HikeSpotResponse> call = AuthApi.getHikeSpots(accessToken);

        call.enqueue(new Callback<HikeSpotResponse>() {
            @Override
            public void onResponse(Call<HikeSpotResponse> call, Response<HikeSpotResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HikeSpot> hikeSpots = response.body().getData();

                    DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);

                    for (HikeSpot hikeSpot : hikeSpots) {
                        dbHelper.insertHikeSpot(hikeSpot);
                    }

                    Log.d("Database", "Hike spots saved successfully");
//                    logAllHikeSpots();
                } else {
                    Log.e("API Error", "Failed to fetch data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<HikeSpotResponse> call, Throwable t) {
                Log.e("Network Error", "Failed to fetch data: " + t.getMessage());
            }
        });
    }

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

//    public void logAllHikeSpots() {
//        DatabaseHelper dbHelper = new DatabaseHelper(this);
//        List<HikeSpot> hikeSpots = dbHelper.getAllHikeSpots();
//
//        for (HikeSpot hikeSpot : hikeSpots) {
//            Log.d("HikeSpot", "ID: " + hikeSpot.getId() +
//                    ", Place: " + hikeSpot.getPlace() +
//                    ", Latitude: " + hikeSpot.getLat() +
//                    ", Longitude: " + hikeSpot.getLong() +
//                    ", Chat ID: " + hikeSpot.getChat_id() +
//                    ", Phone Number: " + hikeSpot.getPhone_number());
//        }
//    }

    private void fetchLocation() {
        LocationService locationService = new LocationService(this);
        locationService.getCurrentLocation(new LocationService.OnLocationFetchedListener() {
            @Override
            public void onLocationFetched(double latitude, double longitude) {
                Log.d("CurrentLocation", "Latitude: " + latitude + ", Longitude: " + longitude);

                LocationUtils locationUtils = new LocationUtils(MainActivity.this);
                locationUtils.checkProximityToHikeSpots(latitude, longitude, "exampleChatId", new LocationUtils.OnProximityAlertListener() {
                    @Override
                    public void onProximityAlertSaved(String hikeSpotName) {
                        Toast.makeText(MainActivity.this, "You are near " + hikeSpotName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("LocationError", errorMessage);
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        fallDetection.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fallDetection.stop();
    }
}