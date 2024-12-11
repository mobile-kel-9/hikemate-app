package com.example.hikemate;

import static java.lang.String.valueOf;

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
import com.example.hikemate.network.AuthApi;
import com.example.hikemate.network.HikeSpotApi;
import com.example.hikemate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.hikemate.services.SOSService;
import com.example.hikemate.utils.BarometerUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private FallDetection fallDetection;
    private BarometerUtil barometerUtil;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final String TAG = "MainActivity";
//    private static final double LATITUDE = -7.2898775; // Placeholder latitude
//    private static final double LONGITUDE = 112.8123612; // Placeholder longitude
    private static final long INTERVAL = 10000; // Interval in milliseconds (10 seconds)

    private Handler handler = new Handler();
    private HikeSpotApi hikeSpotApi;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

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

        barometerUtil = new BarometerUtil(this);
        fallDetection = new FallDetection(this);


        // [BEGIN] TESTING PROXIMITY CHECK
//        double latitude = -7.2898602;
//        double longitude = 112.8123614;
//
//        getHikeSpots(latitude, longitude);

//        HikeSpotApi apiService = RetrofitClient.getHikeSpotApi();
//        String accessToken = "Bearer " + getAccessTokenFromSharedPreferences();
//        Call<HikeSpotProxResponse> call = apiService.getHikeSpotsByLocation(latitude, longitude, accessToken);
//
//        call.enqueue(new Callback<HikeSpotProxResponse>() {
//            @Override
//            public void onResponse(Call<HikeSpotProxResponse> call, Response<HikeSpotProxResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<HikeSpotProxResponse.HikeSpot> hikeSpots = response.body().getData();
//                    // Handle the list of hike spots
//                    for (HikeSpotProxResponse.HikeSpot spot : hikeSpots) {
//                        Log.d("ProximityCheck", "Hike Spot: " + spot.getPlace());
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
        // [END] TESTING PROXIMITY CHECK

        fetchAndStoreHikeSpots();

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
                    // Handle the location update
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("LocationUpdate", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    getHikeSpots(latitude, longitude);
                }
            }
        };
        // [END] REALTIME LOCATION

//        // Initialize Retrofit and HikeSpotApi
//        hikeSpotApi = RetrofitClient.getInstance().create(HikeSpotApi.class);
//
//        // Initialize the Handler
//        handler = new Handler();

        // Start periodic requests
//        startPeriodicRequests();

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
        HikeSpotApi hikespotApi = RetrofitClient.getInstance().create(HikeSpotApi.class);

        String accessToken = "Bearer " + getAccessTokenFromSharedPreferences();
        Call<HikeSpotResponse> call = hikespotApi.getHikeSpots(accessToken);

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
//                        Log.d("ProximityCheck", "Hike Spot: " + spot.getPlace() + "Chat ID: " + spot.getChatId());
//
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

    private void getHikeSpots(double latitude, double longitude) {
        HikeSpotApi apiService = RetrofitClient.getHikeSpotApi();
        String accessToken = "Bearer " + getAccessTokenFromSharedPreferences();
        Call<HikeSpotProxResponse> call = apiService.getHikeSpotsByLocation(latitude, longitude, accessToken);

        call.enqueue(new Callback<HikeSpotProxResponse>() {
            @Override
            public void onResponse(Call<HikeSpotProxResponse> call, Response<HikeSpotProxResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HikeSpotProxResponse.HikeSpot> hikeSpots = response.body().getData();
                    for (HikeSpotProxResponse.HikeSpot spot : hikeSpots) {
                        Log.d("ProximityCheck", "Hike Spot: " + spot.getPlace() + " Chat ID: " + spot.getChatId());
                        float altitude = barometerUtil.getAltitude();
                        // Create an SOSRequest for each hike spot
                        SOSRequest sosRequest = new SOSRequest(
                                String.valueOf(latitude), // Latitude
                                String.valueOf(longitude), // Longitude
                                String.valueOf(altitude), // Example height, modify as needed
                                spot.getPlace(), // Place from the hike spot
                                "Tolong Kami!" // Example message, modify as needed
                        );

                        // Validate the SOSRequest before sending
                        validateSOSRequest(sosRequest);

                        // Call the SOSService to send the SOS
                        SOSService sosService = new SOSService();
                        String chatId = spot.getChatId(); // Get the chat ID from the hike spot
                        String authToken = "Bearer " + getAccessTokenFromSharedPreferences(); // Get the auth token

                        sosService.sendSOS(chatId, authToken, sosRequest, new SOSService.SOSCallback() {
                            @Override
                            public void onSuccess(SOSResponse sosResponse) {
                                // Handle the successful SOS response
                                Log.d("SOSService", "SOS sent successfully for chat ID: " + chatId);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                // Handle the error
                                Log.e("SOSService", "Error sending SOS for chat ID: " + chatId + " - " + throwable.getMessage());
                            }
                        });
                    }
                } else {
                    Log.e(TAG, "Response error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<HikeSpotProxResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void validateSOSRequest(SOSRequest sosRequest) {
        // Log the request details for validation
        Log.d("SOSRequestValidation", "Validating SOS Request:");
        Log.d("SOSRequestValidation", "Latitude: " + sosRequest.getLat());
        Log.d("SOSRequestValidation", "Longitude: " + sosRequest.getLong());
        Log.d("SOSRequestValidation", "Height: " + sosRequest.getHeight());
        Log.d("SOSRequestValidation", "Place: " + sosRequest.getPlace());
        Log.d("SOSRequestValidation", "Message: " + sosRequest.getMessage());

        // Check for any validation issues
        if (sosRequest.getLat() == null || sosRequest.getLat().isEmpty()) {
            Log.e("SOSRequestValidation", "Invalid Latitude");
        }
        if (sosRequest.getLong() == null || sosRequest.getLong().isEmpty()) {
            Log.e("SOSRequestValidation", "Invalid Longitude");
        }
        if (sosRequest.getHeight() == null || sosRequest.getHeight().isEmpty()) {
            Log.e("SOSRequestValidation", "Invalid Height");
        }
        if (sosRequest.getPlace() == null || sosRequest.getPlace().isEmpty()) {
            Log.e("SOSRequestValidation", "Invalid Place");
        }
        if (sosRequest.getMessage() == null || sosRequest.getMessage().isEmpty()) {
            Log.e("SOSRequestValidation", "Invalid Message");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
        fallDetection.start();
        barometerUtil.start();
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
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}