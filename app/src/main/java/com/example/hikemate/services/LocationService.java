//package com.example.hikemate.services;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.util.Log;
//
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;
//
//public class LocationService {
//    private Context context;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//
//    public LocationService(Context context) {
//        this.context = context;
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
//    }
//
//    public void getCurrentLocation(OnLocationFetchedListener listener) {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            listener.onError("Location permission not granted");
//            return;
//        }
//
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    listener.onLocationFetched(latitude, longitude);
//                } else {
//                    listener.onError("Failed to get location");
//                }
//            }
//        });
//    }
//
//    public interface OnLocationFetchedListener {
//        void onLocationFetched(double latitude, double longitude);
//        void onError(String errorMessage);
//    }
//}
