package com.example.hikemate.model;

public class UpdateUserRequest {
    private String name;
    private String birth_date;
    private String country;

    public UpdateUserRequest(String name, String birth_date, String country) {
        this.name = name;
        this.birth_date = birth_date;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String setBirthDate() {
        return birth_date;
    }

    public void setBirthDate(String birth_date) {
        this.birth_date = birth_date;
    }

    public String setCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
