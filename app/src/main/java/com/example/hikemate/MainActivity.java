package com.example.hikemate;

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

import com.example.hikemate.database.DatabaseHelper;
import com.example.hikemate.databinding.ActivityMainBinding;
import com.example.hikemate.model.HikeSpot;
import com.example.hikemate.model.HikeSpotProxResponse;
import com.example.hikemate.model.HikeSpotResponse;
import com.example.hikemate.model.SOSRequest;
import com.example.hikemate.model.SOSResponse;
import com.example.hikemate.network.HikeSpotApi;
import com.example.hikemate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.hikemate.services.HikeSpotService;
import com.example.hikemate.services.SOSService;
import com.example.hikemate.utils.BarometerUtil;
import com.example.hikemate.utils.HikeSpotCallback;
import com.example.hikemate.utils.HikeSpotCallbackImpl;
import com.example.hikemate.utils.ResultHandler;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private FallDetection fallDetection;
    private BarometerUtil barometerUtil;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final String TAG = "HikeSpotCallbackImpl";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private String chatId = null;
    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logOutButton = findViewById(R.id.logout_button);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });

        Log.d("ChatID", "Chat ID: " + chatId);

//        fallDetection = new FallDetection(this);

        HikeSpotApi apiService = RetrofitClient.getHikeSpotApi();

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
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("LocationUpdate", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    String accessToken = getAccessTokenFromSharedPreferences();
                    Log.d("AccessToken", "Access Token: " + accessToken);
                    HikeSpotService hikeSpotService = new HikeSpotService(apiService);
                    HikeSpotCallback callback = new HikeSpotCallbackImpl(new ResultHandler() {
                        @Override
                        public void onResult(String chatId) {
                            chatId = chatId;
                            Log.d("ChatID", "Chat ID: " + chatId);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                        }
                    });
                    hikeSpotService.getHikeSpots(latitude, longitude, "Bearer " + accessToken, callback);
                }
            }
        };
        // [END] REALTIME LOCATION

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

    private String getAccessTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return sharedPreferences.getString("accessToken", "");
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

//    private void getHikeSpots(double latitude, double longitude) {
//        HikeSpotApi apiService = RetrofitClient.getHikeSpotApi();
//        String accessToken = "Bearer " + getAccessTokenFromSharedPreferences();
//        Call<HikeSpotProxResponse> call = apiService.getHikeSpotsByLocation(latitude, longitude, accessToken);
//
//        call.enqueue(new Callback<HikeSpotProxResponse>() {
//            @Override
//            public void onResponse(Call<HikeSpotProxResponse> call, Response<HikeSpotProxResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<HikeSpotProxResponse.HikeSpot> hikeSpots = response.body().getData();
//                    for (HikeSpotProxResponse.HikeSpot spot : hikeSpots) {
//                        Log.d("ProximityCheck", "Hike Spot: " + spot.getPlace() + " Chat ID: " + spot.getChatId());
//                        float altitude = barometerUtil.getAltitude();
//                        // Create an SOSRequest for each hike spot
//                        SOSRequest sosRequest = new SOSRequest(
//                                String.valueOf(latitude), // Latitude
//                                String.valueOf(longitude), // Longitude
//                                String.valueOf(altitude), // Example height, modify as needed
//                                spot.getPlace(), // Place from the hike spot
//                                "Tolong Kami!" // Example message, modify as needed
//                        );
//
//                        // Validate the SOSRequest before sending
//                        validateSOSRequest(sosRequest);
//
//                        // Call the SOSService to send the SOS
//                        SOSService sosService = new SOSService();
//                        String chatId = spot.getChatId(); // Get the chat ID from the hike spot
//                        String authToken = "Bearer " + getAccessTokenFromSharedPreferences(); // Get the auth token
//
//                        sosService.sendSOS(chatId, authToken, sosRequest, new SOSService.SOSCallback() {
//                            @Override
//                            public void onSuccess(SOSResponse sosResponse) {
//                                // Handle the successful SOS response
//                                Log.d("SOSService", "SOS sent successfully for chat ID: " + chatId);
//                            }
//
//                            @Override
//                            public void onFailure(Throwable throwable) {
//                                // Handle the error
//                                Log.e("SOSService", "Error sending SOS for chat ID: " + chatId + " - " + throwable.getMessage());
//                            }
//                        });
//                    }
//                } else {
//                    Log.e(TAG, "Response error: " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HikeSpotProxResponse> call, Throwable t) {
//                Log.e(TAG, "API call failed: " + t.getMessage());
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        fallDetection.start();
        // Start the BarometerUtil
        if (barometerUtil != null) {
            barometerUtil.start();
        } else {
            Log.e("MainActivity", "BarometerUtil is null");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        fallDetection.stop();
        barometerUtil.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}