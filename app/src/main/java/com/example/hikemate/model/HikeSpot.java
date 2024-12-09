package com.example.hikemate.model;

import com.google.gson.annotations.SerializedName;

public class HikeSpot {
    private String id;
    private String place;
    private String lat;
    
    @SerializedName("long")
    private String lng;
    private String chat_id;
    private String phone_number;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPlace() { return place; }
    public void setPlace(String place) { this.place = place; }

    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }

    public String getLong() { return lng; }
    public void setLong(String lng) { this.lng = lng; }

    public String getChat_id() { return chat_id; }
    public void setChat_id(String chat_id) { this.chat_id = chat_id; }

    public String getPhone_number() { return phone_number; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }
}

