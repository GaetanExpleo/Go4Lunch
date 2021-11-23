package com.openclassrooms.go4lunch.repository;

import android.content.Context;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.openclassrooms.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String COLLECTION_NAME = "users";
    private static final String USERNAME_FIELD = "username";
    private static final String LIST_RESTAURANT_LIKED = "listRestaurantLikedId";
    private static volatile UserRepository instance;

    public UserRepository() {
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

    @Nullable
    public FirebaseUser getCurrentUser() {return FirebaseAuth.getInstance().getCurrentUser();}

    @Nullable
    public String getCurrentUserUID(){
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context){return AuthUI.getInstance().signOut(context);}

    // Get the collection reference
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    //Create user in Firestore
    public void createUser(){
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, urlPicture);

            Task<DocumentSnapshot> userData = getUserData();
            userData.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.contains(LIST_RESTAURANT_LIKED)) {
                    userToCreate.setRestaurant((List<Long>) documentSnapshot.get(LIST_RESTAURANT_LIKED));
                }
                getUsersCollection().document(uid).set(userToCreate);
            });
        }
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

    public Task<Void> updateRestaurantList(Long restaurantId){
        String uid = this.getCurrentUserUID();
        if (uid != null){
            List<Long> newList = addRemoveRestaurantLiked(restaurantId);
            return this.getUsersCollection().document(uid).update(LIST_RESTAURANT_LIKED, newList);
        } else {
            return null;
        }
    }

    //Todo : Vérifier la faisabilité avec Denis
    private List<Long> addRemoveRestaurantLiked(Long restaurantId) {
        List<Long> currentListRestaurantLiked = new ArrayList<>();
        getUserData().addOnSuccessListener(documentSnapshot ->
                currentListRestaurantLiked.addAll((List<Long>) documentSnapshot.get(LIST_RESTAURANT_LIKED)));
        if (currentListRestaurantLiked.contains(restaurantId)) {
            currentListRestaurantLiked.remove(restaurantId);
        } else {
            currentListRestaurantLiked.add(restaurantId);
        }
        return currentListRestaurantLiked;
    }
}
