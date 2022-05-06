package com.openclassrooms.go4lunch.repository;

import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import org.jetbrains.annotations.NotNull;

public class LocationRepository {

    private static final int LOCATION_REQUEST_INTERVAL_MS = 60_000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 100;

    @NonNull
    private final FusedLocationProviderClient mFusedLocationProviderClient;

    @NonNull
    private final MutableLiveData<Location> mLocationMutableLiveData = new MutableLiveData<>(null);

    private LocationCallback mCallback;

    public LocationRepository(@NonNull FusedLocationProviderClient fusedLocationProviderClient) {
        mFusedLocationProviderClient = fusedLocationProviderClient;
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocationMutableLiveData;
    }

    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public void startLocationRequest() {
        if (mCallback == null) {
            mCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                    mLocationMutableLiveData.setValue(locationResult.getLastLocation());
                }
            };
        }

        mFusedLocationProviderClient.removeLocationUpdates(mCallback);

        mFusedLocationProviderClient.requestLocationUpdates(
                LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER)
                        .setInterval(LOCATION_REQUEST_INTERVAL_MS),
                mCallback,
                Looper.getMainLooper()
        );
    }

    public void stopLocationRequest() {
        if (mCallback != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mCallback);
        }
    }
}
