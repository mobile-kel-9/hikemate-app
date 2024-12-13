package com.example.hikemate;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.hikemate.model.SOSRequest;
import com.example.hikemate.model.SOSResponse;
import com.example.hikemate.services.SOSService;

public class FallDetection implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Context context;
    private double latitude;
    private double longitude;
    private double height;
    private String chatId;
    private String authToken;
    private String message;
    private SOSService sosService;

    // Cooldown variables
    private long lastSOSTime = 0;
    private static final long COOLDOWN_PERIOD = 5 * 60 * 1000; // 5 minutes in milliseconds

    public FallDetection(Context context, double latitude, double longitude, double height, String chatId, String authToken) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.chatId = chatId;
        this.authToken = authToken;
        this.message = "Tolong Kami!";
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sosService = new SOSService();
    }

    public void start() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double SMV = Math.sqrt(x * x + y * y + z * z);
            if (SMV > 15) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSOSTime > COOLDOWN_PERIOD) {
                    sendSOS();
                    lastSOSTime = currentTime; // Update the last SOS time
                }
                else {
                    Log.d("FallDetection", "Cooldown Time: " + (currentTime-lastSOSTime));
                }
            }
        }
    }

    private void sendSOS() {
        SOSRequest sosRequest = new SOSRequest(String.valueOf(latitude), String.valueOf(longitude), String.valueOf(height), message); // Create a new SOS request
        sosService.sendSOS(chatId, "Bearer " + authToken, sosRequest, new SOSService.SOSCallback() {
            @Override
            public void onSuccess(SOSResponse sosResponse) {
                Log.d("FallDetection", "SOS sent successfully");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("FallDetection", "Failed to send SOS", throwable);
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}