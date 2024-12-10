package com.example.hikemate.utils;

import android.content.Context;
import android.util.Log;

import com.example.hikemate.database.DatabaseHelper;
import com.example.hikemate.model.HikeSpot;

import java.util.List;

public class LocationUtils {

    private static final double PROXIMITY_RADIUS = 50.0;
    private Context context;

    public LocationUtils(Context context) {
        this.context = context;
    }

    // Define the listener interface
    public interface OnProximityAlertListener {
        void onProximityAlertSaved(String hikeSpotName, String chat_id); // Triggered when a proximity alert is successfully saved
        void onError(String errorMessage);               // Triggered if saving fails
    }

    // Check proximity to hike spots and save temporary proximity alert if within radius
    public void checkProximityToHikeSpots(double currentLat, double currentLng, String chatId, OnProximityAlertListener listener) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        List<HikeSpot> hikeSpots = dbHelper.getAllHikeSpots();

        for (HikeSpot hikeSpot : hikeSpots) {
            double hikeLat = Double.parseDouble(hikeSpot.getLat());
            double hikeLng = Double.parseDouble(hikeSpot.getLong());

            double distance = HaversineUtils.calculateDistance(currentLat, currentLng, hikeLat, hikeLng);

            if (distance <= PROXIMITY_RADIUS) {
                Log.d("ProximityCheck", "You are within 50 meters of " + hikeSpot.getPlace());

                // Save the proximity alert to the database
                boolean isSaved = dbHelper.insertTempProximityAlert(chatId, hikeSpot.getPlace());
                if (isSaved) {
                    Log.d("ProximityCheck", "Proximity alert for " + hikeSpot.getPlace() + " saved.");
                    if (listener != null) {
                        listener.onProximityAlertSaved(hikeSpot.getPlace(), hikeSpot.getChat_id());
                    }
                } else {
                    Log.d("ProximityCheck", "Failed to save proximity alert for " + hikeSpot.getPlace());
                    if (listener != null) {
                        listener.onError("Failed to save proximity alert for " + hikeSpot.getPlace());
                    }
                }
            } else {
                Log.d("ProximityCheck", "You are " + distance + " meters away from " + hikeSpot.getPlace());
            }
        }
    }

    // Clear all temporary proximity alerts
    public void clearProximityAlerts() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        dbHelper.clearTempProximityAlerts();
        Log.d("ProximityCheck", "All temporary proximity alerts cleared.");
    }
}