package com.openclassrooms.go4lunch.ui.listview;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.permission_checker.PermissionChecker;
import com.openclassrooms.go4lunch.pojo.Result;
import com.openclassrooms.go4lunch.repository.LocationRepository;
import com.openclassrooms.go4lunch.repository.PlaceRepository;
import com.openclassrooms.go4lunch.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ListviewViewModel extends ViewModel {

    @NonNull
    private final PermissionChecker mPermissionChecker;
    @NonNull
    private final UserManager mUserManager;
    @NonNull
    private final PlaceRepository mPlaceRepository;
    @NonNull
    private final LocationRepository mLocationRepository;

    private final Application mApplication = MainApplication.getApplication();

    private final LiveData<Location> mLocationLiveData;
    private final LiveData<List<String>> mChosenRestaurantList;
    private final LiveData<List<Result>> mNearbyPlaceLiveData;

    public ListviewViewModel(
            @NonNull UserManager userManager,
            @NonNull PlaceRepository placeRepository,
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository
    ) {
        mUserManager = userManager;
        mPlaceRepository = placeRepository;
        mPermissionChecker = permissionChecker;
        mLocationRepository = locationRepository;

        mLocationLiveData = mLocationRepository.getLocationLiveData();
        mChosenRestaurantList = mUserManager.getSelectedRestaurantsId();
        mNearbyPlaceLiveData = mPlaceRepository.getNearbyPlace();
    }

    public void executeGetNearbyPlace(Location location) {
        mPlaceRepository.executeGetNearbyPlace(location);
    }

    public LiveData<Location> getLocation() {
        return mLocationLiveData;
    }

    public LiveData<List<ListviewViewState>> getNearbyPlaceLiveData() {

        MediatorLiveData<List<ListviewViewState>> nearbyPlaceMediatorLiveData = new MediatorLiveData<>();

        nearbyPlaceMediatorLiveData.addSource(mNearbyPlaceLiveData, result -> {
            List<ListviewViewState> listviewViewState = combine(
                    result,
                    mChosenRestaurantList.getValue());
            if (listviewViewState != null) {
                nearbyPlaceMediatorLiveData.setValue(listviewViewState);
            }
        });

        nearbyPlaceMediatorLiveData.addSource(mChosenRestaurantList, strings -> {
            List<ListviewViewState> listviewViewState = combine(
                    mNearbyPlaceLiveData.getValue(),
                    strings);
            if (listviewViewState != null) {
                nearbyPlaceMediatorLiveData.setValue(listviewViewState);
            }
        });

        return nearbyPlaceMediatorLiveData;
    }

    private List<ListviewViewState> combine(List<Result> results, List<String> listIdRestaurant) {
        List<ListviewViewState> listRestaurant = new ArrayList<>();

        if (results != null) {
            for (Result result : results) {
                String photoReference = (result.getPhotos() != null) ? result.getPhotos().get(0).getPhotoReference() : null;
                String photo = photoReference == null ? null :
                        String.format(Locale.FRANCE, "%sphoto?photo_reference=%s&maxwidth=%d&key=%s",
                                Util.baseUrl,
                                photoReference,
                                result.getPhotos().get(0).getWidth(),
                                BuildConfig.google_map_key);

                String name = result.getName();
                String address = result.getVicinity().split(",")[0];

                String isOpen;
                if (result.getOpeningHours() == null) {
                    isOpen = mApplication.getResources().getString(R.string.opening_hour_no_information);
                } else if (result.getOpeningHours().getOpenNow()) {
                    isOpen = mApplication.getString(R.string.opening_hour_opened);
                } else {
                    isOpen = mApplication.getString(R.string.opening_hour_closed);
                }
                Location restaurantLocation = new Location(LocationManager.GPS_PROVIDER);
                restaurantLocation.setLongitude(result.getGeometry().getLocation().getLng());
                restaurantLocation.setLatitude(result.getGeometry().getLocation().getLat());
                String distance = Math.round(Objects.requireNonNull(mLocationLiveData.getValue()).distanceTo(restaurantLocation)) + "m";

                String placeId = result.getPlaceId();
                float rate = result.getRating() != null ?
                        (float) (result.getRating() / 5 * 3) : 0;

                String workmatesInterested = "(" + Collections.frequency(listIdRestaurant, placeId) + ")";

                ListviewViewState listviewViewState = new ListviewViewState(
                        photo, name, address, isOpen, distance,
                        workmatesInterested, rate, placeId
                );

                listRestaurant.add(listviewViewState);
            }

            Collections.sort(listRestaurant);
        }
        return listRestaurant;
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
        if (mPermissionChecker.hasLocationPermission()) {
            mLocationRepository.startLocationRequest();
        } else {
            mLocationRepository.stopLocationRequest();
        }
    }
}
