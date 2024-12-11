package com.example.hikemate.model;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class SOSResponse {
    private String id;            // Unique identifier for the SOS request
    private String lat;           // Latitude as a String
    @SerializedName("long")
    private String lng;          // Longitude as a String
    private String place;         // Place name
    private String message;       // SOS message
    private String chatId;        // Chat ID
    private String userId;        // User ID
    private ZonedDateTime createdAt; // Timestamp for when the SOS was created
    private ZonedDateTime updatedAt; // Timestamp for when the SOS was last updated

    // Constructor
    public SOSResponse(String id, String lat, String lng, String place, String message,
                       String chatId, String userId, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.place = place;
        this.message = message;
        this.chatId = chatId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SOSResponse{" +
                "id='" + id + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", place='" + place + '\'' +
                ", message='" + message + '\'' +
                ", chatId='" + chatId + '\'' +
                ", userId='" + userId + '\'' +
                ", created At=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
