package com.openclassrooms.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.permission_checker.PermissionChecker;
import com.openclassrooms.go4lunch.repository.PlaceRepository;
import com.openclassrooms.go4lunch.ui.detail.RestaurantDetailViewModel;

import org.jetbrains.annotations.NotNull;

public class ViewModelFactoryParameter extends ViewModelProvider.NewInstanceFactory implements ViewModelProvider.Factory {

    @NonNull
    private final String mParameter;
    @NonNull
    private final UserManager mUserManager;
    @NonNull
    private final PermissionChecker mPermissionChecker;
    @NonNull
    private final PlaceRepository mPlaceRepository;

    public ViewModelFactoryParameter(
            @NonNull String parameter) {
        this.mParameter = parameter;
        mUserManager = UserManager.getInstance();
        mPermissionChecker = PermissionChecker.getInstance();
        mPlaceRepository = PlaceRepository.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public <T extends ViewModel> T create(@NonNull @NotNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantDetailViewModel.class)) {
            return (T) new RestaurantDetailViewModel(mParameter, mUserManager, mPermissionChecker, mPlaceRepository);
        }
        throw new IllegalArgumentException("Unknown viewModel class");
    }
}
