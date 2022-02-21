package com.openclassrooms.go4lunch.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.manager.UserManager;
import com.openclassrooms.go4lunch.model.ChosenRestaurant;
import com.openclassrooms.go4lunch.model.User;
import com.openclassrooms.go4lunch.permission_checker.PermissionChecker;
import com.openclassrooms.go4lunch.pojo.ResultDetail;
import com.openclassrooms.go4lunch.repository.PlaceRepository;
import com.openclassrooms.go4lunch.utils.Util;

import java.util.List;
import java.util.Locale;

public class RestaurantDetailViewModel extends ViewModel {

    private final UserManager mUserManager;
    private final PermissionChecker mPermissionChecker;
    private final PlaceRepository mPlaceRepository;

    private final LiveData<ResultDetail> mResultDetailLiveData;
    private final LiveData<List<String>> mLikedRestaurantListLiveData;
    private final LiveData<String> mSelectedRestaurantLiveData;
    private final LiveData<List<User>> mInterestedWorkmateListLiveData;

    private String mPlaceName;
    private String mPlaceAddress;


    public RestaurantDetailViewModel(String parameter, UserManager userManager, PermissionChecker permissionChecker, PlaceRepository placeRepository) {
        mUserManager = userManager;
        mPermissionChecker = permissionChecker;
        mPlaceRepository = placeRepository;

        mResultDetailLiveData = mPlaceRepository.getDetailPlace();
        mLikedRestaurantListLiveData = mUserManager.getLikedRestaurants();
        mSelectedRestaurantLiveData = mUserManager.getCurrentUserSelectedRestaurant();
        mInterestedWorkmateListLiveData = mUserManager.getInterestedUsers(parameter);
    }

    public void executeRequestPlaceDetail(String placeId) {
        mPlaceRepository.executeRequestPlaceDetail(placeId);
    }

    public void onLikeButtonClicked(String placeId) {
        mUserManager.updateLikedRestaurants(placeId);
    }

    public void onSelectButtonClicked(String placeId) {
        ChosenRestaurant selectedRestaurant = new ChosenRestaurant(placeId, mPlaceName, mPlaceAddress);
        mUserManager.updateSelectedRestaurant(selectedRestaurant);
    }

    private float calculationRate(Double rating) {
        return rating.floatValue() / 5 * 3;
    }

    public LiveData<RestaurantDetailViewState> getRestaurantDetailLiveData() {

        MediatorLiveData<RestaurantDetailViewState> restaurantDetailMediatorLiveData = new MediatorLiveData<>();

        restaurantDetailMediatorLiveData.addSource(mResultDetailLiveData, resultDetail -> {
                    RestaurantDetailViewState restaurantDetailViewState = combine(
                            resultDetail,
                            mLikedRestaurantListLiveData.getValue(),
                            mSelectedRestaurantLiveData.getValue(),
                            mInterestedWorkmateListLiveData.getValue());
                    if (restaurantDetailViewState != null)
                        restaurantDetailMediatorLiveData.setValue(restaurantDetailViewState);
                }
        );

        restaurantDetailMediatorLiveData.addSource(mLikedRestaurantListLiveData, likedRestaurantList -> {
                    RestaurantDetailViewState restaurantDetailViewState = combine(
                            mResultDetailLiveData.getValue(),
                            likedRestaurantList,
                            mSelectedRestaurantLiveData.getValue(),
                            mInterestedWorkmateListLiveData.getValue());
                    if (restaurantDetailViewState != null)
                        restaurantDetailMediatorLiveData.setValue(restaurantDetailViewState);
                }
        );

        restaurantDetailMediatorLiveData.addSource(mSelectedRestaurantLiveData, selectedRestaurant ->
                {
                    RestaurantDetailViewState restaurantDetailViewState = combine(
                            mResultDetailLiveData.getValue(),
                            mLikedRestaurantListLiveData.getValue(),
                            selectedRestaurant,
                            mInterestedWorkmateListLiveData.getValue());
                    if (restaurantDetailViewState != null)
                        restaurantDetailMediatorLiveData.setValue(restaurantDetailViewState);
                }
        );

        restaurantDetailMediatorLiveData.addSource(mInterestedWorkmateListLiveData, interestedWorkmateList -> {
                    RestaurantDetailViewState restaurantDetailViewState = combine(
                            mResultDetailLiveData.getValue(),
                            mLikedRestaurantListLiveData.getValue(),
                            mSelectedRestaurantLiveData.getValue(),
                            interestedWorkmateList);
                    if (restaurantDetailViewState != null)
                        restaurantDetailMediatorLiveData.setValue(restaurantDetailViewState);
                }
        );

        return restaurantDetailMediatorLiveData;
    }

    private RestaurantDetailViewState combine(
            ResultDetail restaurantDetail,
            List<String> likedRestaurantList,
            String chosenRestaurant,
            List<User> interestedWorkmateList) {
        if (restaurantDetail != null) {
            String photoReference = (restaurantDetail.getPhotos() != null) ? restaurantDetail.getPhotos().get(0).getPhotoReference() : null;
            String photo = photoReference == null ? null :
                    String.format(Locale.FRANCE, "%sphoto?photo_reference=%s&maxwidth=%d&key=%s",
                            Util.baseUrl,
                            photoReference,
                            restaurantDetail.getPhotos().get(0).getWidth(),
                            BuildConfig.google_map_key);

            mPlaceName = restaurantDetail.getName();

            mPlaceAddress = String.format("%s %s",
                    restaurantDetail.getAddressComponents().get(0).getLongName(),
                    restaurantDetail.getAddressComponents().get(1).getLongName());
            float rate = RestaurantDetailViewModel.this.calculationRate(restaurantDetail.getRating());
            String phoneNumber = restaurantDetail.getFormattedPhoneNumber();
            String website = restaurantDetail.getWebsite();

            boolean isFavorite = likedRestaurantList != null && likedRestaurantList.contains(restaurantDetail.getPlaceId());
            boolean isSelected = chosenRestaurant != null && chosenRestaurant.equals(restaurantDetail.getPlaceId());

            return new RestaurantDetailViewState(photo,
                    mPlaceName, mPlaceAddress, rate, isSelected, phoneNumber, isFavorite,
                    website, interestedWorkmateList);
        }
        return null;
    }

    public void listenDocument() {
        mUserManager.listenDocument();
    }

    public void stopListenDocument() {
        mUserManager.stopListenDocument();
    }

    public boolean hasCallPermission() {
        return mPermissionChecker.hasCallPermission();
    }

}
