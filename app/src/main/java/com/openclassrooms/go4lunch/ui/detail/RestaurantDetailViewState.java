package com.openclassrooms.go4lunch.ui.detail;

import androidx.annotation.Nullable;

import com.openclassrooms.go4lunch.model.User;

import java.util.List;

public class RestaurantDetailViewState {

    @Nullable
    private final String photo;
    private final String name;
    private final String address;
    private final float rate;
    private final boolean isSelected;
    @Nullable
    private final String phoneNumber;
    private final boolean isFavorite;
    @Nullable
    private final String website;
    @Nullable
    private final List<User> workmates;

    public RestaurantDetailViewState(
            @Nullable String photo,
            String name,
            String address,
            float rate,
            boolean isSelected,
            @Nullable String phoneNumber,
            boolean isFavorite,
            @Nullable String website,
            @Nullable List<User> workmates
    ) {
        this.photo = photo;
        this.name = name;
        this.address = address;
        this.rate = rate;
        this.isSelected = isSelected;
        this.phoneNumber = phoneNumber;
        this.isFavorite = isFavorite;
        this.website = website;
        this.workmates = workmates;
    }

    @Nullable
    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getRate() {
        return rate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    @Nullable
    public String getWebsite() {
        return website;
    }

    @Nullable
    public List<User> getWorkmates() {
        return workmates;
    }
}
