package com.openclassrooms.go4lunch.ui.listview;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ListviewViewState implements Comparable<ListviewViewState> {

    @Nullable
    private final String photo;
    private final String name;
    private final String address;
    private final String isOpen;
    private final String distance;
    private final String workmatesInterested;
    private final float rate;
    private final String placeId;

    public ListviewViewState(
            @Nullable String photo,
            String name,
            String address,
            String isOpen,
            String distance,
            String workmatesInterested,
            float rate,
            String placeId) {
        this.photo = photo;
        this.name = name;
        this.address = address;
        this.isOpen = isOpen;
        this.distance = distance;
        this.workmatesInterested = workmatesInterested;
        this.rate = rate;
        this.placeId = placeId;
    }

    @Nullable
    public String getPhoto() {return photo;}
    public String getName() {return name;}
    public String getAddress() {return address;}
    public String isOpen() {return isOpen;}
    public String getDistance() {return distance;}
    public String getWorkmatesInterested() {return workmatesInterested;}
    public float getRate() {return rate;}
    public String getPlaceId() {return placeId;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListviewViewState that = (ListviewViewState) o;
        return isOpen.equals(that.isOpen) &&
                Float.compare(that.rate, rate) == 0 &&
                Objects.equals(photo, that.photo) &&
                name.equals(that.name) &&
                address.equals(that.address) &&
                Objects.equals(distance, that.distance) &&
                Objects.equals(workmatesInterested, that.workmatesInterested);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address);
    }

    @Override
    public int compareTo(ListviewViewState o) {
        return (Integer.parseInt(this.distance.replace("m", "")) - Integer.parseInt(o.distance.replace("m", "")));
    }
}
