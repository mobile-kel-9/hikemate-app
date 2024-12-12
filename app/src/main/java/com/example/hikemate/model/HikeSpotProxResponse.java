package com.example.hikemate.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HikeSpotProxResponse {
    private List<HikeSpot> data;

    public List<HikeSpot> getData() {
        return data;
    }

    public void setData(List<HikeSpot> data) {
        this.data = data;
    }

    public static class HikeSpot {
        private String id;
        private String lat;
        @SerializedName("long")
        private String lng;
        private String place;
        private String chat_id;
        private String phone_number;
        private String created_at;
        private String updated_at;

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

        public String getChatId() {
            return chat_id;
        }

        public void setChatId(String chat_id) {
            this.chat_id = chat_id;
        }

        public String getPhoneNumber() {
            return phone_number;
        }

        public void setPhoneNumber(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getCreatedAt() {
            return created_at;
        }

        public void setCreatedAt(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdatedAt() {
            return updated_at;
        }

        public void setUpdatedAt(String updated_at) {
            this.updated_at = updated_at;
        }

        @Override
        public String toString() {
            return "HikeSpot{" +
                    "id='" + id + '\'' +
                    ", lat='" + lat + '\'' +
                    ", lng='" + lng + '\'' +
                    ", place='" + place + '\'' +
                    ", chatId='" + chat_id + '\'' +
                    ", phoneNumber='" + phone_number + '\'' +
                    ", createdAt='" + created_at + '\'' +
                    ", updatedAt='" + updated_at + '\'' +
                    '}';
        }
    }
}
