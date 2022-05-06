package com.openclassrooms.go4lunch.manager;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.model.ChosenRestaurant;
import com.openclassrooms.go4lunch.model.User;
import com.openclassrooms.go4lunch.repository.UserRepository;

import java.util.List;

public class UserManager {

    private static volatile UserManager instance;
    private final UserRepository mUserRepository;

    private User mCurrentUserData = null;

    public UserManager() {
        mUserRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context) {
        return mUserRepository.signOut(context);
    }

    public void createUser() {
        mUserRepository.createUser();
    }

    public void updateLikedRestaurants(String placeId) {
        mUserRepository.updateLikedRestaurant(placeId);
    }

    public void updateSelectedRestaurant(ChosenRestaurant selectedRestaurant) {
        mUserRepository.updateSelectedRestaurant(selectedRestaurant);
    }

    public LiveData<List<String>> getLikedRestaurants() {
        return mUserRepository.getLikedRestaurants();
    }

    public Query getAllWorkmates() {
        return mUserRepository.getAllWorkmates();
    }

    public User getUserData() {
        // Get the user from Firestore and cast it to a User model object
        mUserRepository.getUserData().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if (document.exists() && document != null) {
                    mCurrentUserData = document.toObject(User.class);
                }
            }
        });
        return mCurrentUserData;
    }

    public LiveData<String> getCurrentUserSelectedRestaurant() {
        return mUserRepository.getCurrentUserSelectedRestaurant();
    }

    public void listenDocument() {
        mUserRepository.listenDocument();
    }

    public void stopListenDocument() {
        mUserRepository.stopListenDocument();
    }

    public LiveData<List<User>> getInterestedUsers(String placeId) {
        return mUserRepository.getInterestedUsers(placeId);
    }

    public LiveData<List<String>> getSelectedRestaurantsId() {
        return mUserRepository.getSelectedRestaurantsId();
    }
}
