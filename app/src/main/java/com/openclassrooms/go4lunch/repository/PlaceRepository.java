package com.openclassrooms.go4lunch.repository;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.go4lunch.pojo.Place;
import com.openclassrooms.go4lunch.pojo.PlaceDetail;
import com.openclassrooms.go4lunch.pojo.Result;
import com.openclassrooms.go4lunch.pojo.ResultDetail;
import com.openclassrooms.go4lunch.utils.PlaceStreams;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;

public class PlaceRepository {

    private final MutableLiveData<List<Result>> mPlaceListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<ResultDetail> mDetailListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Result>> mPredictionsMutableLiveData = new MutableLiveData<>();

    public PlaceRepository() {
    }

    private static class PlaceRepositoryHolder {
        private final static PlaceRepository INSTANCE = new PlaceRepository();
    }

    public static synchronized PlaceRepository getInstance() {
        return PlaceRepositoryHolder.INSTANCE;
    }

    public LiveData<List<Result>> getNearbyPlace() {
        return mPlaceListMutableLiveData;
    }

    public void executeGetNearbyPlace(Location location) {
        Disposable nearbyPlaceDisposable = PlaceStreams.streamFetchNearbyPlace(location)
                .subscribeWith(new DisposableObserver<Place>() {
                    @Override
                    public void onNext(@NotNull Place place) {
                        mPlaceListMutableLiveData.setValue(place.getResults());
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<ResultDetail> getDetailPlace() {
        return mDetailListMutableLiveData;
    }

    public void executeRequestPlaceDetail(String placeId) {
        Disposable detailDisposable = PlaceStreams.streamFetchPlaceDetail(placeId)
                .subscribeWith(new DisposableObserver<PlaceDetail>() {
                    @Override
                    public void onNext(@NotNull PlaceDetail placeDetail) {
                        mDetailListMutableLiveData.setValue(placeDetail.getResult());
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public LiveData<List<Result>> getPredictions() {
        return mPredictionsMutableLiveData;
    }

    public void executePredictions(Location location, String input) {
        DisposableSingleObserver predictionDisposable = PlaceStreams.streamFetchPlaceDetailsOfPredictions(location, input)
                .subscribeWith(new DisposableSingleObserver<List<Result>>() {
                    @Override
                    public void onSuccess(@NotNull List<Result> predictions) {
                        mPlaceListMutableLiveData.setValue(predictions);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                    }
                });
    }
}
