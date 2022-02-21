package com.openclassrooms.go4lunch.ui.mapview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.FragmentMapviewBinding;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.ui.ViewModelFactory;
import com.openclassrooms.go4lunch.ui.listview.OnRestaurantClickedListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MapviewFragment extends Fragment implements
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mGoogleMap;
    private static final int DEFAULT_ZOOM = 15;

    private MapviewViewModel mMapviewViewModel;
    private FragmentMapviewBinding mBinding;

    private OnRestaurantClickedListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mMapviewViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapviewViewModel.class);

        mBinding = FragmentMapviewBinding.inflate(inflater, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initPermission();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_mapview_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapviewViewModel.refresh();

//        UserManager userManager = UserManager.getInstance();
//        userManager.listenDocument();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
        mMapviewViewModel.stopLocationRequest();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (OnRestaurantClickedListener) context;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setMenuVisibility(true);
    }

    private void initPermission() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                0
        );
    }

    @SuppressLint("MissingPermission")
    private void initObservable() {
        mMapviewViewModel.getLocationLiveData()
                .observe(getViewLifecycleOwner(), location -> {
                    if (location != null) {
                        moveToLocation(location);
                        mMapviewViewModel.executeGetNearbyPlace(location);
                    }
                });
        mMapviewViewModel.getNearbyPlace()
                .observe(getViewLifecycleOwner(), mapViewStates -> {
                    mGoogleMap.clear();
                    this.addMarker(mapViewStates);
                });
        mMapviewViewModel.getPermissionMutableLiveData()
                .observe(getViewLifecycleOwner(), hasPermission ->
                        mGoogleMap.setMyLocationEnabled(hasPermission)
                );
    }

    private void addMarker(List<MapViewState> mapViewStates) {

        for (MapViewState viewState : mapViewStates) {
            String placeId = viewState.getPlaceId();

            int backgroundColor = android.R.color.holo_red_light;
            int iconColor = R.color.black;

            if (viewState.isSelected()) {
                backgroundColor = android.R.color.holo_green_light;
                iconColor = R.color.white;
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions
                    .title(viewState.getName() + " : " + viewState.getAddress())
                    .position(viewState.getLatLng())
                    .icon(bitmapDescriptorFromVector(getContext(), getResources().getColor(backgroundColor), getResources().getColor(iconColor)));
            Marker marker = mGoogleMap.addMarker(markerOptions);
            if (marker != null) {
                marker.setTag(placeId);
            }
        }

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @ColorInt int backgroundColor, @ColorInt int iconColor) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.baseline_place_black_48);
        background.setBounds(0, 0, 100, 100);
        background.setTint(backgroundColor);
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_restaurant_24);
        vectorDrawable.setBounds(32, 28, 36 + 32, 28 + 32);
        vectorDrawable.setTint(iconColor);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void moveToLocation(Location location) {
        if (location != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;
            mGoogleMap.setOnMarkerClickListener(MapviewFragment.this);

            if (UserManager.getInstance().isCurrentUserLogged())
                MapviewFragment.this.initObservable();
        }
    };

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        mListener.onRestaurantClicked(marker.getTag().toString());
        return true;
    }
}
