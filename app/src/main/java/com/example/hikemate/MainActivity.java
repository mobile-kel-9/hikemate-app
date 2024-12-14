package com.example.hikemate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hikemate.databinding.ActivityMainBinding;
import com.example.hikemate.model.HikeSpot;
import com.example.hikemate.model.HikeSpotResponse;
import com.example.hikemate.model.MeResponse;
import com.example.hikemate.model.UserProfile;
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.HikeSpotApi;
import com.example.hikemate.network.RetrofitClient;
import com.example.hikemate.ui.home.HomeViewModel;
import com.example.hikemate.utils.GetMeCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.hikemate.services.HikeSpotService;
import com.example.hikemate.utils.BarometerUtil;
import com.example.hikemate.utils.HikeSpotCallback;
import com.example.hikemate.utils.HikeSpotCallbackImpl;
import com.example.hikemate.utils.ResultHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private FallDetection fallDetection;
//    private BarometerUtil barometerUtil;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private double latitude;
    private double longitude;
    private float height = 50;
    private String accessToken = null;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String chatId = null;
    private Button logOutButton, getMeButton;
    private HomeViewModel homeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.setHeight(height);

        logOutButton = findViewById(R.id.logout_button);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

        getMeButton = findViewById(R.id.getme_button);

        getMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = "Bearer " + getAccessTokenFromSharedPreferences();
                getMe(token, new GetMeCallback() {
                    @Override
                    public void onSuccess(String name, String country) {
                        Log.d("GetMe", "Retrieved Name: " + name);
                        Log.d("GetMe", "Retrieved Country: " + country);
                        homeViewModel.setName(name);
                        homeViewModel.setCountry(country);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("GetMe", "Error: " + error);
                    }
                });
            }
        });

//        getMe("Bearer " + getAccessTokenFromSharedPreferences());
//        homeViewModel.setName(place);
        Log.d("ChatID", "Chat ID: " + chatId);

        HikeSpotApi apiService = RetrofitClient.getHikeSpotApi();

//        barometerUtil = new BarometerUtil(this, new BarometerUtil.AltitudeCallback() {
//            @Override
//            public void onAltitudeChanged(float newAltitude) {
//                height = newAltitude;
//                Log.d("Barometer", "Alt: " + height);
//            }
//        });
//        barometerUtil.start();


        // [BEGIN] REALTIME LOCATION
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d("LocationUpdate", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    accessToken = getAccessTokenFromSharedPreferences();
                    Log.d("AccessToken", "Access Token: " + accessToken);
                    HikeSpotService hikeSpotService = new HikeSpotService(apiService);
                    HikeSpotCallback callback = new HikeSpotCallbackImpl(new ResultHandler() {
                        @Override
                        public void onResult(String chatId, String place) {
                            chatId = chatId;
                            Log.d("ChatID", "Chat ID: " + chatId);
                            Log.d("Place", "Place: " + place);
                            homeViewModel.setPlace(place);
                            FallDetection fallDetection = new FallDetection(MainActivity.this, latitude, longitude, height, chatId, accessToken);
                            fallDetection.start();
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }
                    });
//                    getMe(accessToken);
                    hikeSpotService.getHikeSpots(latitude, longitude, "Bearer " + accessToken, callback);
                }
            }
        };
        // [END] REALTIME LOCATION

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_home) {
                setTitle("Home");
            } else if (destination.getId() == R.id.navigation_dashboard) {
                setTitle("Dashboard");
            } else if (destination.getId() == R.id.navigation_feed) {
                setTitle("Feed");
            } else if (destination.getId() == R.id.navigation_notifications) {
                setTitle("Notifications");
            }
        });

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

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
    }

    private void getMe(String token, GetMeCallback callback) {
        AuthApi authService = RetrofitClient.getAuthApi();
        Call<MeResponse> call = authService.validateToken("Bearer " + token);
        call.enqueue(new Callback<MeResponse>() {
            @Override
            public void onResponse(Call<MeResponse> call, Response<MeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile userProfile = response.body().getData();
                    Log.d("GetMe", "User  ID: " + userProfile.getId());
                    Log.d("GetMe", "Name: " + userProfile.getName());
                    Log.d("GetMe", "Email: " + userProfile.getEmail());
                    Log.d("GetMe", "Country: " + userProfile.getCountry());
                    Log.d("GetMe", "Birth Date: " + userProfile.getBirthDate());
                    Log.d("GetMe", "Role: " + userProfile.getRole());
                    Log.d("GetMe", "Image Path: " + userProfile.getImagePath());

                    callback.onSuccess(userProfile.getName(), userProfile.getCountry());
                } else {
                    Log.e("GetMe", "Response error: " + response.code());
                    callback.onError("Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MeResponse> call, Throwable t) {
                callback.onError("API call failed: " + t.getMessage());
            }
        });
    }

    // [BEGIN] REALTIME LOCATION
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(this, "Location permission is required to get location updates", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // [END] REALTIME LOCATION

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
//        barometerUtil.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        if (fallDetection != null) {
            fallDetection.stop();
        }
//        if (barometerUtil != null) {
//            barometerUtil.stop();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}