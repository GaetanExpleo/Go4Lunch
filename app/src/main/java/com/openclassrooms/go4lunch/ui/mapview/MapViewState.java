package com.openclassrooms.go4lunch.ui.mapview;

import com.google.android.gms.maps.model.LatLng;

public class MapViewState {

    private final String mName;
    private final String mAddress;
    private final LatLng mLatLng;
    private final String mPlaceId;
    private final boolean mIsSelected;

    public MapViewState(
            String name,
            String address,
            LatLng latLng,
            String placeId,
            boolean isSelected
    ) {
        mName = name;
        mAddress = address;
        mLatLng = latLng;
        mPlaceId = placeId;
        mIsSelected = isSelected;
    }

    // --- GETTER ---
    public String getName() {return mName;}
    public String getAddress() {return mAddress;}
    public LatLng getLatLng() {return mLatLng;}
    public String getPlaceId() {return mPlaceId;}
    public boolean isSelected() {return mIsSelected;}
}
