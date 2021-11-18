package com.openclassrooms.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.List;

public class User {

    private String uid;
    private String username;
    @Nullable
    private List<Long> listRestaurantLikedId;
    @Nullable
    private String urlPicture;

    public User() {
    }

    public User(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    // --- GETTER ---
    public String getUid() {
        return uid;
    }
    public String getUsername() {
        return username;
    }
    public List<Long> getRestaurant() {
        return listRestaurantLikedId;
    }
    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    // --- SETTER ---
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setRestaurant(@Nullable List<Long> restaurant) {
        this.listRestaurantLikedId = restaurant;
    }
    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
