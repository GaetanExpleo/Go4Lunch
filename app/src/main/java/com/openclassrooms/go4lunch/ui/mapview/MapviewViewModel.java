package com.openclassrooms.go4lunch.ui.mapview;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.permission_checker.PermissionChecker;
import com.openclassrooms.go4lunch.pojo.Result;
import com.openclassrooms.go4lunch.repository.LocationRepository;
import com.openclassrooms.go4lunch.repository.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

public class MapviewViewModel extends ViewModel {

    @NonNull
    private final PermissionChecker mPermissionChecker;
    @NonNull
    private final PlaceRepository mPlaceRepository;
    @NonNull
    private final LocationRepository mLocationRepository;

    private final MutableLiveData<Boolean> mPermissionMutableLiveData = new MutableLiveData<>();

    private final LiveData<List<Result>> mNearbyPlaceLiveData;
    private final LiveData<List<String>> mSelectedRestaurantsId;
    private final LiveData<List<Result>> mPredictionsLiveData;

    public MapviewViewModel(
            @NonNull UserManager userManager,
            @NonNull PlaceRepository placeRepository,
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository) {
        mPlaceRepository = placeRepository;
        mPermissionChecker = permissionChecker;
        mLocationRepository = locationRepository;

        mNearbyPlaceLiveData = mPlaceRepository.getNearbyPlace();
        mSelectedRestaurantsId = userManager.getSelectedRestaurantsId();
        mPredictionsLiveData = mPlaceRepository.getPredictions();
    }

    public void executeGetNearbyPlace(Location location) {
        mPlaceRepository.executeGetNearbyPlace(location);
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocationRepository.getLocationLiveData();
    }

    public LiveData<List<MapViewState>> getNearbyPlace() {

        MediatorLiveData<List<MapViewState>> listMapViewStateMediatorLiveData = new MediatorLiveData<>();

        listMapViewStateMediatorLiveData.addSource(mNearbyPlaceLiveData, results -> {
            List<MapViewState> mapViewStates = combine(
                    results,
                    mSelectedRestaurantsId.getValue(),
                    mPredictionsLiveData.getValue());
            if (mapViewStates != null)
                listMapViewStateMediatorLiveData.setValue(mapViewStates);
        });

        listMapViewStateMediatorLiveData.addSource(mSelectedRestaurantsId, strings -> {
            List<MapViewState> mapViewStates = combine(
                    mNearbyPlaceLiveData.getValue(),
                    strings,
                    mPredictionsLiveData.getValue());
            if (mapViewStates != null)
                listMapViewStateMediatorLiveData.setValue(mapViewStates);
        });

        listMapViewStateMediatorLiveData.addSource(mPredictionsLiveData, results -> {
            List<MapViewState> mapViewStates = combine(
                    mNearbyPlaceLiveData.getValue(),
                    mSelectedRestaurantsId.getValue(),
                    results);
            if (mapViewStates != null) {
                listMapViewStateMediatorLiveData.setValue(mapViewStates);
            }
        });

        return listMapViewStateMediatorLiveData;
    }

    private List<MapViewState> combine(List<Result> nearbyPlaces, List<String> selectedRestaurant, List<Result> predictions) {
        List<MapViewState> listResult = new ArrayList<>();

        if (predictions != null && predictions.size() > 0) {
            for (Result prediction : predictions) {
                listResult.add(getResultData(prediction, selectedRestaurant));
            }
        } else {
            if (nearbyPlaces != null) {
                for (Result nearbyPlace : nearbyPlaces) {
                    listResult.add(getResultData(nearbyPlace, selectedRestaurant));
                }
            }
        }

        return listResult;
    }

    private MapViewState getResultData(Result place, List<String> selectedRestaurant) {
        if (selectedRestaurant != null) {
            String name = place.getName();
            String address = place.getVicinity().split(",")[0];
            LatLng latLng = new LatLng(
                    place.getGeometry().getLocation().getLat(),
                    place.getGeometry().getLocation().getLng());
            String placeId = place.getPlaceId();
            boolean isSelected = selectedRestaurant.contains(placeId);

            return new MapViewState(name, address, latLng, placeId, isSelected);
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
        if (mPermissionChecker.hasLocationPermission()) {
            mLocationRepository.startLocationRequest();
            mPermissionMutableLiveData.setValue(true);
        } else {
            mLocationRepository.stopLocationRequest();
            mPermissionMutableLiveData.setValue(false);
        }
    }

    public void stopLocationRequest() {
        mLocationRepository.stopLocationRequest();
    }

    public LiveData<Boolean> getPermissionMutableLiveData() {
        return mPermissionMutableLiveData;
    }
}
