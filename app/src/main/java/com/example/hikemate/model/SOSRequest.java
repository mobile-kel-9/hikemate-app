package com.example.hikemate.model;

import com.google.gson.annotations.SerializedName;

public class SOSRequest {
    private String lat;
    @SerializedName("long")
    private String lng;
    private String height;
    private String message;

    public SOSRequest(String lat, String lng, String height, String message) {
        this.lat = lat;
        this.lng = lng;
        this.height = height;
        this.message = message;
    }

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
                ", message='" + message + '\'' +
                '}';
    }
}