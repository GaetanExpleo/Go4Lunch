package com.openclassrooms.go4lunch.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.go4lunch.manager.UserManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // DESIGN
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActivityMainBinding mBinding;
    private BottomNavigationView mBottomNavigationView;

    // NAVIGATION HEADER
    private View headerView;
    private ImageView navProfilePicture;

    private static final int RC_SIGN_IN = 123;
    private ActivityResultLauncher<Intent> mResultLauncher;

    private final UserManager mUserManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomView();
        this.startSignInActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithUserData();
    }

    private void configureToolbar() {
        this.mToolbar = mBinding.activityMainToolbar;
        setSupportActionBar(mToolbar);
    }

    private void configureDrawerLayout() {
        this.mDrawerLayout = mBinding.activityMainDrawerLayout;
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        this.mNavigationView = mBinding.activityMainNavView;
        mNavigationView.setNavigationItemSelectedListener(this);
        headerView = mNavigationView.getHeaderView(0);
        navProfilePicture = headerView.findViewById(R.id.nav_view_imageview);
        updateUIWithUserData();
    }

    private void configureBottomView() {
        this.mBottomNavigationView = mBinding.activityMainBottomNavigation;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapview, R.id.navigation_listview, R.id.navigation_workmate)
                .build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_fragment);
        try {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(mBinding.activityMainBottomNavigation, navController);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUIWithUserData() {
        if (mUserManager.isCurrentUserLogged()) {
            FirebaseUser user = mUserManager.getCurrentUser();

            if (user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            } else {
                setDefaultProfilePicture();
            }
            setTextUserData(user);
        }
    }

    private void startSignInActivity() {
        if (!mUserManager.isCurrentUserLogged()) {
            List<AuthUI.IdpConfig> providers =
                    Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                            new AuthUI.IdpConfig.TwitterBuilder().build());

            // TODO : voir comment mettre en place ResultLauncher
//            mResultLauncher = registerForActivityResult(
//                    new ActivityResultContracts.StartActivityForResult(),
//                    result -> handlerResponseAfterSignIn(result)
//            );
//
//            Intent intent = new Intent(AuthUI.getInstance()
//                    .createSignInIntentBuilder()
//                    .setTheme(R.style.LoginTheme)
//                    .setAvailableProviders(providers)
//                    .setLogo(R.drawable.go4lunch_logo_white_text_en)
//                    .setIsSmartLockEnabled(false, true)
//                    .build());
//
//            mResultLauncher.launch(intent);

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.LoginTheme)
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.go4lunch_logo_white_text_en)
                            .setIsSmartLockEnabled(false, true)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handlerResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handlerResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
//    private void handlerResponseAfterSignIn(ActivityResult result) {

        IdpResponse response = IdpResponse.fromResultIntent(data);


        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mUserManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
            } else {
                startSignInActivity();
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError() != null) {
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }
        }

    }

    private void setTextUserData(FirebaseUser user) {
        TextView navUsername = headerView.findViewById(R.id.nav_view_username_textview);
        TextView navEmail = headerView.findViewById(R.id.nav_view_email_textview);

        String username = TextUtils.isEmpty(user.getDisplayName()) ?
                getString(R.string.info_no_username_found) : user.getDisplayName();
        String email = TextUtils.isEmpty(user.getEmail()) ?
                getString(R.string.info_no_email_found) : user.getEmail();

        navUsername.setText(username);
        navEmail.setText(email);
    }

    private void setProfilePicture(Uri profilePictureUrl) {
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(navProfilePicture);
    }

    private void setDefaultProfilePicture() {
        Glide.with(this)
                .load(R.drawable.default_profile)
                .apply(RequestOptions.circleCropTransform())
                .into(navProfilePicture);
    }

    private void showSnackBar(String message) {
        Snackbar.make(mBinding.activityMainDrawerLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_drawer_your_lunch:
                break;
            case R.id.activity_main_drawer_settings:
                break;
            case R.id.activity_main_drawer_logout:
                mUserManager.signOut(this).addOnSuccessListener(aVoid -> this.startSignInActivity());
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}