package com.openclassrooms.go4lunch.manager;

import android.content.Context;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.openclassrooms.go4lunch.model.User;
import com.openclassrooms.go4lunch.repository.UserRepository;

public class UserManager {

    private static volatile UserManager instance;
    private UserRepository mUserRepository;

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

    public void createUser(){
        mUserRepository.createUser();
    }

    public Task<User> getUserData() {
        // Get the user from Firestore and cast it to a User model object
        return mUserRepository.getUserData().continueWith((Continuation<DocumentSnapshot, User>) task ->
                task.getResult().toObject(User.class)
        );
    }
}
