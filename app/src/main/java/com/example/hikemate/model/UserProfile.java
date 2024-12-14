package com.example.hikemate.model;

public class UserProfile {
    private String id;
    private String name;
    private String email;
    private String country;
    private String birth_date;
    private String role;
    private String image_path;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getBirthDate() {
        return birth_date;
    }

    public String getRole() {
        return role;
    }

    public String getImagePath() {
        return image_path;
    }
}