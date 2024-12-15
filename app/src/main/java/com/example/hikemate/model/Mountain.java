package com.example.hikemate.model;

public class Mountain {
    private String name;
    private double latitude;
    private double longitude;
    private String description;
    private int difficulty;
    private double height;

    public Mountain(String name, double latitude, double longitude, String description, int difficulty, double height) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.difficulty = difficulty;
        this.height = height;
    }

    // Getters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public int getDifficulty() { return difficulty; }
    public double getHeight() { return height; }
}
