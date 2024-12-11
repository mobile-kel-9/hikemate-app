package com.example.hikemate.model;

import com.google.gson.annotations.SerializedName;

public class SOSRequest {
    private String lat;      // Latitude as a String
    @SerializedName("long")
    private String lng;     // Longitude as a String
    private String height;   // Height as a String
    private String place;    // Place name
    private String message;  // SOS message

    // Constructor
    public SOSRequest(String lat, String lng, String height, String place, String message) {
        this.lat = lat;
        this.lng = lng;
        this.height = height;
        this.place = place;
        this.message = message;
    }

    // Getters and Setters
    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return lng;
    }

    public void setLong(String lng) {
        this.lng = lng;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SOSRequest{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", height='" + height + '\'' +
                ", place='" + place + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}