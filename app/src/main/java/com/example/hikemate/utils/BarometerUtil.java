package com.example.hikemate.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class BarometerUtil implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private float pressure;
    private float altitude;
    private AltitudeCallback callback;

    public interface AltitudeCallback {
        void onAltitudeChanged(float newAltitude);
    }

    public BarometerUtil(Context context, AltitudeCallback callback) {
        this.callback = callback;
        if (pressureSensor == null) {
            Log.d("LocationUpdate", "No barometer sensor found");
        }
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }
    }

    public void start() {
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            Log.d("Barometer", "Sensor data received");
            pressure = event.values[0];
            altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            if (callback != null) {
                callback.onAltitudeChanged(altitude);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}