package com.openclassrooms.go4lunch.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ActivityMainBinding;
import com.openclassrooms.go4lunch.ui.listview.ListviewFragment;
import com.openclassrooms.go4lunch.ui.mapview.MapviewFragment;
import com.openclassrooms.go4lunch.ui.workmate.WorkmateFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;

    private ActivityMainBinding mBinding;

    private ListviewFragment mListviewFragment;
    private MapviewFragment mMapviewFragment;
    private WorkmateFragment mWorkmateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        this.configureToolbar();
        this.configureDrawerLayout();
        this.configureBottomView();

//        this.verifySignInState();
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
    }

    private void configureBottomView() {
        this.mBottomNavigationView = mBinding.activityMainBottomNavigation;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_mapview, R.id.navigation_listview, R.id.navigation_workmate)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.activity_main_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.activityMainBottomNavigation, navController);
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
                break;
            case R.id.navigation_mapview:
                break;
            case R.id.navigation_listview:
                break;
            case R.id.navigation_workmate:
                break;
        }

        this.mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}