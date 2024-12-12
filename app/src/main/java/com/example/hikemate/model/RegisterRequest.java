package com.example.hikemate.model;

public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String birth_date;
    private String country;

    public RegisterRequest(String name, String email, String password, String birth_date, String country) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birth_date = birth_date;
        this.country = country;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", birth_date='" + birth_date + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
