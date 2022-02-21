package com.openclassrooms.go4lunch.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationServices;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.permission_checker.PermissionChecker;
import com.openclassrooms.go4lunch.repository.LocationRepository;
import com.openclassrooms.go4lunch.repository.PlaceRepository;
import com.openclassrooms.go4lunch.ui.listview.ListviewViewModel;
import com.openclassrooms.go4lunch.ui.mapview.MapviewViewModel;
import com.openclassrooms.go4lunch.ui.workmate.WorkmateViewModel;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private volatile static ViewModelFactory sInstance;

    @NonNull
    private final PermissionChecker mPermissionChecker;
    @NonNull
    private final LocationRepository mLocationRepository;
    @NonNull
    private final UserManager mUserManager;
    @NonNull
    private final PlaceRepository mPlaceRepository;

    public ViewModelFactory(
            @NonNull UserManager userManager,
            @NonNull PlaceRepository placeRepository,
            @NonNull PermissionChecker permissionChecker,
            @NonNull LocationRepository locationRepository) {
        mUserManager = userManager;
        mPlaceRepository = placeRepository;
        mPermissionChecker = permissionChecker;
        mLocationRepository = locationRepository;
    }

    public static ViewModelFactory getInstance() {
        if (sInstance == null) {
            synchronized (ViewModelFactory.class) {
                if (sInstance == null) {
                    Application application = MainApplication.getApplication();
                    sInstance = new ViewModelFactory(
                            UserManager.getInstance(),
                            PlaceRepository.getInstance(),
                            PermissionChecker.getInstance(),
                            new LocationRepository(
                                    LocationServices.getFusedLocationProviderClient(
                                            application
                                    )
                            ));
                }
            }
        }
        return sInstance;
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MapviewViewModel.class)) {
            return (T) new MapviewViewModel(mUserManager, mPlaceRepository, mPermissionChecker, mLocationRepository);
        }
        if (modelClass.isAssignableFrom(ListviewViewModel.class)) {
            return (T) new ListviewViewModel(mUserManager, mPlaceRepository, mPermissionChecker, mLocationRepository);
        }
        if (modelClass.isAssignableFrom(WorkmateViewModel.class)) {
            return (T) new WorkmateViewModel(mUserManager);
        }
        if (modelClass.isAssignableFrom(MainActivityViewModel.class)) {
            return (T) new MainActivityViewModel(mUserManager, mLocationRepository, mPlaceRepository);
        }
        throw new IllegalArgumentException("Unknown viewModel class");
    }
}