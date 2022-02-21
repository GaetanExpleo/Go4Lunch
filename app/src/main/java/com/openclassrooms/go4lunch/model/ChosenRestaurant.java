package com.openclassrooms.go4lunch.model;

import java.util.Objects;

public class ChosenRestaurant {

    private String placeId;
    private String placeName;
    private String placeAddress;

    public ChosenRestaurant() {
    }

    public ChosenRestaurant(String placeId, String placeName, String placeAddress) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
    }

    // --- GETTER ---
    public String getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    // --- SETTER ---
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChosenRestaurant that = (ChosenRestaurant) o;
        return Objects.equals(placeId, that.placeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placeId);
    }
}
