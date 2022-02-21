package com.openclassrooms.go4lunch.ui.workmate;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.Query;
import com.openclassrooms.go4lunch.manager.UserManager;

public class WorkmateViewModel extends ViewModel {

    private final UserManager mUserManager;

    public WorkmateViewModel(UserManager userManager) {
        mUserManager = userManager;
    }

    public Query getAllWorkmates() {
        return mUserManager.getAllWorkmates();
    }
}
