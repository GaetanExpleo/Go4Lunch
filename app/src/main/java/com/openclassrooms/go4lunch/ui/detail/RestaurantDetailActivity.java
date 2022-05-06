package com.openclassrooms.go4lunch.ui.detail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.TextViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.databinding.ActivityRestaurantDetailBinding;
import com.openclassrooms.go4lunch.ui.ViewModelFactoryParameter;

import org.jetbrains.annotations.NotNull;

public class RestaurantDetailActivity extends AppCompatActivity {

    public static final String PLACE_ID = "PLACE_ID";
    private static final int CALL_RC = 1;

    private ActivityRestaurantDetailBinding mBinding;
    private ImageView mImageView;
    private TextView mNameTextView;
    private RatingBar mRatingBar;
    private FloatingActionButton mSelectedFAB;
    private TextView mAddressTextView;
    private TextView mCallTextView;
    private TextView mLikeTextView;
    private TextView mWebsiteTextView;
    private RecyclerView mRecyclerView;

    private RestaurantDetailAdapter mAdapter;

    private String mPlaceId;
    private String mPlaceName;
    private String mPlaceAddress;
    private RestaurantDetailViewModel mViewModel;

    private String mPhoneNumber;
    private boolean mIsLiked;
    private String mWebsite;

    private int[] colors;
    private ColorStateList mColorStateList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRestaurantDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            mPlaceId = getIntent().getExtras().getString(PLACE_ID);
        }

        mViewModel = new ViewModelProvider(this, new ViewModelFactoryParameter(mPlaceId)).get(RestaurantDetailViewModel.class);

        this.initDesign();
        this.listenDocument();
        this.initStateColors();
        this.initListener();
        this.initObserver();
        this.initRecyclerview();
        mViewModel.executeRequestPlaceDetail(mPlaceId);
    }

    @Override
    protected void onPause() {
        mViewModel.stopListenDocument();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void listenDocument() {
        mViewModel.listenDocument();
    }

    private void initDesign() {
        mImageView = mBinding.activityRestaurantDetailImage;
        mNameTextView = mBinding.activityRestaurantDetailName;
        mRatingBar = mBinding.activityRestaurantDetailRatingBar;
        mSelectedFAB = mBinding.activityRestaurantDetailFab;
        mAddressTextView = mBinding.activityRestaurantDetailAddress;
        mCallTextView = mBinding.activityRestaurantDetailCall;
        mLikeTextView = mBinding.activityRestaurantDetailLike;
        mWebsiteTextView = mBinding.activityRestaurantDetailWeb;
        mRecyclerView = mBinding.activityRestaurantDetailRecyclerview;
    }

    private void initStateColors() {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_enabled},
        };

        colors = new int[]{
                getResources().getColor(R.color.orange),
                getResources().getColor(R.color.grey)
        };

        mColorStateList = new ColorStateList(states, colors);
    }

    private void initListener() {

        mSelectedFAB.setOnClickListener(v -> mViewModel.onSelectButtonClicked(mPlaceId));

        mCallTextView.setOnClickListener(v -> {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_RC);
            }
            launchCallActivity();
        });

        mLikeTextView.setOnClickListener(v -> mViewModel.onLikeButtonClicked(mPlaceId));
    }

    private void setDrawableColor(Drawable[] compoundDrawables, int color) {
        compoundDrawables[1].setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }

    private void initObserver() {
        mViewModel.getRestaurantDetailLiveData().observe(this, restaurantDetailViewState -> {
            Glide.with(RestaurantDetailActivity.this)
                    .load(restaurantDetailViewState.getPhoto())
                    .into(mImageView);
            mPlaceName = restaurantDetailViewState.getName();

            mNameTextView.setText(mPlaceName);
            mNameTextView.setSelected(true);

            mPlaceAddress = restaurantDetailViewState.getAddress();
            mAddressTextView.setText(mPlaceAddress);
            mRatingBar.setRating(restaurantDetailViewState.getRate());

            mSelectedFAB.setImageResource(
                    restaurantDetailViewState.isSelected() ?
                            R.drawable.ic_restaurant_selected :
                            R.drawable.ic_restaurant_unselected
            );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TextViewCompat.setCompoundDrawableTintList(mWebsiteTextView, mColorStateList);
                mWebsiteTextView.setTextColor(mColorStateList);
                TextViewCompat.setCompoundDrawableTintList(mCallTextView, mColorStateList);
                mCallTextView.setTextColor(mColorStateList);
            }

            mPhoneNumber = restaurantDetailViewState.getPhoneNumber();
            mCallTextView.setEnabled(mPhoneNumber != null);

            mIsLiked = restaurantDetailViewState.isFavorite();
            Drawable[] drawables = mLikeTextView.getCompoundDrawables();
            if (mIsLiked) {
                setDrawableColor(drawables, colors[0]);
                mLikeTextView.setTextColor(colors[0]);
            } else {
                setDrawableColor(drawables, colors[1]);
                mLikeTextView.setTextColor(colors[1]);
            }

            mWebsite = restaurantDetailViewState.getWebsite();
            if (mWebsite != null) {
                mWebsiteTextView.setEnabled(true);
                mWebsiteTextView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mWebsite));
                    startActivity(intent);
                });
            } else {
                mWebsiteTextView.setEnabled(false);
            }

            mAdapter.submitList(restaurantDetailViewState.getWorkmates());
        });
    }

    private void initRecyclerview() {
        mAdapter = new RestaurantDetailAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void launchCallActivity() {
        if (mViewModel.hasCallPermission()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mPhoneNumber));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CALL_RC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCallActivity();
            }
        }
    }
}
