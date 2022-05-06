package com.openclassrooms.go4lunch.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.openclassrooms.go4lunch.model.ChosenRestaurant;
import com.openclassrooms.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public static final String COLLECTION_NAME = "users";
    public static final String RESTAURANT_LIKED_FIELD = "listRestaurantLikedId";
    public static final String RESTAURANT_SELECTED_FIELD = "chosenRestaurant";
    public static final String CHOSEN_RESTAURANT_ID_FIELD = "chosenRestaurant.placeId";
    public static final String CHOSEN_RESTAURANT_NAME_FIELD = "chosenRestaurant.placeName";
    public static final String CHOSEN_RESTAURANT_ADDRESS_FIELD = "chosenRestaurant.placeAddress";
    public static final String USER_UID_FIELD = "uid";
    public static final String USER_NAME_FIELD = "username";
    private static volatile UserRepository instance;

    private final MutableLiveData<List<String>> likedRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> currentUserSelectedRestaurantMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> selectedRestaurantsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<User>> interestedUsersMutableLiveData = new MutableLiveData<>();

    private final FirebaseFirestore db;
    private ListenerRegistration mRegistration;

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    // --- AUTH USER ---
    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    public String getCurrentUserUID() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    // --- USER FIRESTORE ---

    // Get the collection reference
    public CollectionReference getUsersCollection() {
        return db.collection(COLLECTION_NAME);
    }

    private DocumentReference getUserDocument(String uid) {
        return getUsersCollection().document(uid);
    }

    // Get user data from Firestore
    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    //Create user in Firestore
    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();
            userData.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (!document.exists()) {
                        userToCreate.setListRestaurantLikedId(new ArrayList<>());
                        userToCreate.setChosenRestaurant(new ChosenRestaurant("", "", ""));
                        getUserDocument(uid).set(userToCreate);
                    }
                }

            });
        }
    }

    // --- DATA FROM FIRESTORE ---

    public LiveData<List<String>> getLikedRestaurants() {
        return likedRestaurantsMutableLiveData;
    }

    public LiveData<String> getCurrentUserSelectedRestaurant() {
        return currentUserSelectedRestaurantMutableLiveData;
    }

    public LiveData<List<String>> getSelectedRestaurantsId() {
        List<String> selectedRestaurants = new ArrayList<>();

        this.getUsersCollection()
                .whereNotEqualTo(USER_UID_FIELD, getCurrentUserUID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (!user.getChosenRestaurant().getPlaceId().equals(""))
                                selectedRestaurants.add(user.getChosenRestaurant().getPlaceId());
                        }
                        selectedRestaurantsMutableLiveData.setValue(selectedRestaurants);
                    } else {
                        Log.w("TAG", "getSelectedRestaurantsId error: ", task.getException());
                    }
                });
        return selectedRestaurantsMutableLiveData;
    }

    public LiveData<List<User>> getInterestedUsers(String placeId) {
        List<User> interestedUsers = new ArrayList<>();

        this.getUsersCollection()
                .whereNotEqualTo(USER_UID_FIELD, getCurrentUserUID())
                .whereEqualTo(CHOSEN_RESTAURANT_ID_FIELD, placeId)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    interestedUsers.add(user);
                }
                interestedUsersMutableLiveData.setValue(interestedUsers);
            } else {
                Log.w("TAG", "getInterestedUsers error: ", task.getException());
            }
        });

        return interestedUsersMutableLiveData;
    }

    public void updateLikedRestaurant(String placeId) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                DocumentSnapshot snapshot = transaction.get(getUserDocument(uid));

                User user = snapshot.toObject(User.class);

                List<String> restaurantLiked = user.getListRestaurantLikedId();
                if (!restaurantLiked.contains(placeId)) {
                    restaurantLiked.add(placeId);
                } else {
                    restaurantLiked.remove(placeId);
                }
                transaction.update(getUserDocument(uid), RESTAURANT_LIKED_FIELD, restaurantLiked);

                return null;
            });
        }
    }

    public void updateSelectedRestaurant(ChosenRestaurant selectedRestaurant) {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            db.runTransaction((Transaction.Function<String>) transaction -> {
                DocumentSnapshot snapshot = transaction.get(getUserDocument(uid));

                User user = snapshot.toObject(User.class);
                ChosenRestaurant chosenRestaurant = user.getChosenRestaurant();

                if (chosenRestaurant.equals(selectedRestaurant)) {
                    transaction.update(getUserDocument(uid), RESTAURANT_SELECTED_FIELD, new ChosenRestaurant("", "", ""));
                } else {
                    transaction.update(getUserDocument(uid), RESTAURANT_SELECTED_FIELD, selectedRestaurant);
                }
                return null;
            });
        }
    }

    public void listenDocument() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {

            mRegistration = getUserDocument(uid).addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.d("TAG", "Listen failed", error);
                    return;
                }

                if (value != null && value.exists()) {
                    User user = value.toObject(User.class);
                    likedRestaurantsMutableLiveData.setValue(user.getListRestaurantLikedId());
                    currentUserSelectedRestaurantMutableLiveData.setValue(user.getChosenRestaurant().getPlaceId());
                } else {
                    Log.d("TAG", "Current data : null");
                }
            });
        }
    }

    public void stopListenDocument() {
        if (mRegistration != null)
            mRegistration.remove();
    }

    public Query getAllWorkmates() {
        return this.getUsersCollection()
                .whereNotEqualTo(USER_UID_FIELD, getCurrentUserUID());
    }
}
