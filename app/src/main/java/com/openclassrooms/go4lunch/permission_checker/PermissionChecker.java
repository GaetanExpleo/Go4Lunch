package com.openclassrooms.go4lunch.permission_checker;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.openclassrooms.go4lunch.MainApplication;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class PermissionChecker {

    @NonNull
    private final Application mApplication = MainApplication.getApplication();

    public PermissionChecker() {
    }

    private static class PermissionCheckerHolder {
        private final static PermissionChecker INSTANCE = new PermissionChecker();
    }

    public static synchronized PermissionChecker getInstance() {
        return PermissionCheckerHolder.INSTANCE;
    }

    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(mApplication, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
    }

    public boolean hasCallPermission() {
        return ContextCompat.checkSelfPermission(mApplication, CALL_PHONE) == PERMISSION_GRANTED;
    }
}
