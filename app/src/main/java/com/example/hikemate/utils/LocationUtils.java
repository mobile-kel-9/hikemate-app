package com.example.hikemate.utils;

import android.content.Context;
import android.util.Log;

import com.example.hikemate.database.DatabaseHelper;
import com.example.hikemate.model.HikeSpot;

import java.util.List;

public class LocationUtils {

    private static final double PROXIMITY_RADIUS = 50.0; // 50 meters

    private Context context;

    // Constructor to pass context from MainActivity (or wherever it's being used)
    public LocationUtils(Context context) {
        this.context = context;
    }
    public void checkProximityToHikeSpots(double currentLat, double currentLng) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<HikeSpot> hikeSpots = dbHelper.getAllHikeSpots();

        for (HikeSpot hikeSpot : hikeSpots) {
            double hikeLat = Double.parseDouble(hikeSpot.getLat());
            double hikeLng = Double.parseDouble(hikeSpot.getLong());

            double distance = HaversineUtils.calculateDistance(currentLat, currentLng, hikeLat, hikeLng);

            if (distance <= PROXIMITY_RADIUS) {
                Log.d("ProximityCheck", "You are within 50 meters of " + hikeSpot.getPlace());
            } else {
                Log.d("ProximityCheck", "You are " + distance + " meters away from " + hikeSpot.getPlace());
            }
        }
    }
}
