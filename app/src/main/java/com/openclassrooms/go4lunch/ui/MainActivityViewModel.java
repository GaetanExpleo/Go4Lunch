package com.openclassrooms.go4lunch.ui;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.notification.NotificationWorker;
import com.openclassrooms.go4lunch.repository.LocationRepository;
import com.openclassrooms.go4lunch.repository.PlaceRepository;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivityViewModel extends ViewModel {

    private static final String NOTIFICATION_UNIQUE_WORK_NAME = "notification";
    private final UserManager mUserManager;
    private final LocationRepository mLocationRepository;
    private final PlaceRepository mPlaceRepository;

    private final WorkManager mWorkManager;

    public MainActivityViewModel(UserManager userManager, LocationRepository locationRepository, PlaceRepository placeRepository) {
        mUserManager = userManager;
        mLocationRepository = locationRepository;
        mPlaceRepository = placeRepository;
        mWorkManager = WorkManager.getInstance(MainApplication.getApplication());
    }

    public void executeGetNearbyPlace() {
        mPlaceRepository.executeGetNearbyPlace(mLocationRepository.getLocationLiveData().getValue());
    }

    public LiveData<String> getCurrentUserSelectedRestaurant() {
        return mUserManager.getCurrentUserSelectedRestaurant();
    }

    public void listenDocument() {
        mUserManager.listenDocument();
    }

    public void stopListenDocument() {
        mUserManager.stopListenDocument();
    }

    public void executePredictions(String newText) {
        Location location = mLocationRepository.getLocationLiveData().getValue();
        mPlaceRepository.executePredictions(location, newText);
    }

    public void activateNotification(boolean isChecked) {
        if (isChecked) {
            long delay = getDelay();

            PeriodicWorkRequest notificationRequest =
                    new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.DAYS)
                            .setInitialDelay(delay, TimeUnit.MINUTES)
                            .build();

            mWorkManager.enqueueUniquePeriodicWork(NOTIFICATION_UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, notificationRequest);
        } else {
            mWorkManager.cancelUniqueWork(NOTIFICATION_UNIQUE_WORK_NAME);
        }
    }

    private long getDelay() {
        String time = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.FRANCE)
                .format(new Date());

        int hoursBeforeMidday, minutesBeforeMidday = 0;

        if (!time.equals("12:00")) {
            String[] timeSplit = time.split(":");
            int hour = Integer.parseInt(timeSplit[0]);
            int minute = Integer.parseInt(timeSplit[1]);

            if (hour >= 12) {
                hoursBeforeMidday = 24 - (hour - 12);
            } else {
                hoursBeforeMidday = 12 - hour;
            }

            if (minute > 0) {
                hoursBeforeMidday--;
                minutesBeforeMidday = 60 - minute;
            }
        } else {
            return 0;
        }

        return hoursBeforeMidday * 60 + minutesBeforeMidday;
    }
}
