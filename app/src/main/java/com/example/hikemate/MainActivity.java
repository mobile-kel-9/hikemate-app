package com.example.hikemate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hikemate.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private FallDetection fallDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button altitudeButton = findViewById(R.id.button_altitude);
//
//        altitudeButton.setOnClickListener(view -> {
//            Intent intent = new Intent(MainActivity.this, BarometerAltitude.class);
//            startActivity(intent);
//            finish();
//        });

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