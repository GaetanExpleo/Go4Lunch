package com.openclassrooms.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class User {

    private String uid;
    private String username;
    @Nullable
    private List<String> listRestaurantLikedId;
    @Nullable
    private ChosenRestaurant chosenRestaurant;
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

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    @Nullable
    public List<String> getListRestaurantLikedId() {
        return listRestaurantLikedId;
    }

    @Nullable
    public ChosenRestaurant getChosenRestaurant() {
        return chosenRestaurant;
    }

    // --- SETTER ---
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setListRestaurantLikedId(@Nullable List<String> listRestaurantLikedId) {
        this.listRestaurantLikedId = listRestaurantLikedId;
    }

    public void setChosenRestaurant(@Nullable ChosenRestaurant chosenRestaurant) {
        this.chosenRestaurant = chosenRestaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uid.equals(user.uid) &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username);
    }
}
