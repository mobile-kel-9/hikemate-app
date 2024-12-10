package com.example.hikemate.model;

public class TemporaryHikeSpot {
    private String chatId;
    private String hikeSpotName;

    // Constructor
    public TemporaryHikeSpot(String chatId, String hikeSpotName) {
        this.chatId = chatId;
        this.hikeSpotName = hikeSpotName;
    }

    // Getter for chatId
    public String getChatId() {
        return chatId;
    }

    // Getter for hikeSpotName
    public String getHikeSpotName() {
        return hikeSpotName;
    }
}