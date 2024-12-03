package com.example.hikemate;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class FallDetection implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Context context;

    public FallDetection(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
            if (SMV > 20) {
                showFallAlert();
            }
        }
    }

    private void showFallAlert() {
        // Use an Intent to start the alert activity
        Intent intent = new Intent(context, SOSAlert.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required when starting an activity from a non-activity context
        context.startActivity(intent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}
