package com.openclassrooms.go4lunch.ui;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
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
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.model.User;
import com.openclassrooms.go4lunch.ui.detail.RestaurantDetailActivity;
import com.openclassrooms.go4lunch.ui.listview.OnRestaurantClickedListener;
import com.openclassrooms.go4lunch.ui.workmate.OnWorkmateClickedListener;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnWorkmateClickedListener,
        OnRestaurantClickedListener {

    private static final String SETTINGS = "SETTINGS";
    private static final String NOTIFICATION = "notification";
    private final Application mApplication = MainApplication.getApplication();

    // DESIGN
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActivityMainBinding mBinding;
    private BottomNavigationView mBottomNavigationView;

    // NAVIGATION HEADER
    private View headerView;
    private ImageView navProfilePicture;

    // DESIGN SETTINGS DIALOG
    private SwitchMaterial mSwitchMaterial;

    private SharedPreferences mPreferences;

    private ActivityResultLauncher<Intent> mResultLauncher;
    private Intent intent;

    private UserManager mUserManager = UserManager.getInstance();

    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainActivityViewModel.class);


        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureBottomView();
        this.configureActivityLauncher();
        this.startSignInActivity();
//        this.initListenDocument();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIWithUserData();
    }

    @Override
    protected void onStop() {
        mViewModel.stopListenDocument();
        super.onStop();
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

    private void initListenDocument() {
        mViewModel.listenDocument();
    }

    private void startSignInActivity() {
        if (!mUserManager.isCurrentUserLogged()) {
            mResultLauncher.launch(intent);
        } else {
            initListenDocument();
        }
    }

    private void configureActivityLauncher() {
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build(),
                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                        new AuthUI.IdpConfig.TwitterBuilder().build());

        mResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handlerResponseAfterSignIn
        );

        intent = new Intent(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .setLogo(R.drawable.go4lunch_logo_white_text_en)
                .setIsSmartLockEnabled(false, true)
                .build());
    }

    private void handlerResponseAfterSignIn(ActivityResult result) {

        IdpResponse response = IdpResponse.fromResultIntent(result.getData());

        if (result.getResultCode() == RESULT_OK) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mBottomNavigationView.getSelectedItemId() != R.id.navigation_workmate) {
            getMenuInflater().inflate(R.menu.menu_activity_main, menu);

            MenuItem menuItem = menu.findItem(R.id.menu_search);

            SearchView searchView = (SearchView) menuItem.getActionView();
            searchView.setQueryHint(getResources().getString(R.string.searchview_hint));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                boolean predictionDone = false;

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (newText.length() > 3) {
                        predictionDone = true;
                        mViewModel.executePredictions(newText);
                    } else if (predictionDone) {
                        predictionDone = false;
                        mViewModel.executeGetNearbyPlace();
                    }
                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.activity_main_drawer_your_lunch:
                String selectedRestaurant = mViewModel.getCurrentUserSelectedRestaurant().getValue();

                if (!selectedRestaurant.equals("") && selectedRestaurant != null) {
                    Intent yourLunchIntent = new Intent(this, RestaurantDetailActivity.class);
                    yourLunchIntent.putExtra(RestaurantDetailActivity.PLACE_ID, mViewModel.getCurrentUserSelectedRestaurant().getValue());
                    startActivity(yourLunchIntent);
                } else {
                    showSnackBar(getString(R.string.snackbar_no_selected_restaurant));
                }
                break;
            case R.id.activity_main_drawer_settings:
                showSettingsDialog();
                break;
            case R.id.activity_main_drawer_logout:
                mUserManager.signOut(this).addOnSuccessListener(aVoid -> this.startSignInActivity());
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_fragment_settings, null);
        builder.setView(view);

        mSwitchMaterial = view.findViewById(R.id.dialog_fragment_settings_notification);

        mSwitchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> mViewModel.activateNotification(isChecked));

        builder.setTitle(R.string.settings_title);

        builder.setPositiveButton(R.string.settings_dialog_positive_title, (dialog, which) -> {
            savePreferences();
            dialog.dismiss();
            showSnackBar(getString(R.string.settings_dialog_positive_message));
        });

        builder.setNegativeButton(R.string.settings_dialog_negative_button, (dialog, which) -> {
            showSnackBar(getString(R.string.setting_dialog_negative_message));
            dialog.cancel();
        });

        AlertDialog alert = builder.create();

        if (mPreferences != null) {
            boolean notification = mPreferences.getBoolean(NOTIFICATION, false);

            mSwitchMaterial.setChecked(notification);
        }

        alert.show();
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = mPreferences.edit();

        editor.putBoolean(NOTIFICATION, mSwitchMaterial.isChecked());

        editor.apply();
    }

    private void updateUIWithUserData() {
        if (mUserManager.isCurrentUserLogged()) {
            FirebaseUser user = mUserManager.getCurrentUser();

            Uri photoUrl = user.getPhotoUrl();

            if (photoUrl == null) {
                User currentUser = mUserManager.getUserData();

                if (currentUser != null) {
                    String photo = currentUser.getUrlPicture();

                    if (photo == null) {
                        setDefaultProfilePicture();
                    } else {
                        setProfilePicture(Uri.parse(photo));
                    }
                }

            } else {
                setProfilePicture(photoUrl);
            }

            setTextUserData(user);
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
    public void onWorkmateClicked(@Nullable String placeId) {
        if (placeId != null && !placeId.isEmpty()) {
            launchDetailActivity(placeId);
        } else {
            Snackbar.make(mDrawerLayout, mApplication.getResources().getString(R.string.snackbar_no_selected_restaurant), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRestaurantClicked(@NonNull @NotNull String placeId) {
        launchDetailActivity(placeId);
    }

    public void launchDetailActivity(String placeId) {
        Intent detailIntent = new Intent(this, RestaurantDetailActivity.class);
        detailIntent.putExtra(RestaurantDetailActivity.PLACE_ID, placeId);
        startActivity(detailIntent);
    }
}