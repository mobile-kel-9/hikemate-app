package com.example.hikemate.model;

public class UpdateUser {
    private String id;
    private String name;
    private String email;
    private String password;
    private String birth_date;
    private String country;
    private String image_path;
    private String user_type;
    private String created_at;
    private String updated_at;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public String getCountry() {
        return country;
    }

    public String getImage_path() {
        return image_path;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
