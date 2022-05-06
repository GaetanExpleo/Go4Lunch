package com.openclassrooms.go4lunch.utils;

import android.location.Location;

import com.openclassrooms.go4lunch.pojo.Place;
import com.openclassrooms.go4lunch.pojo.PlaceDetail;
import com.openclassrooms.go4lunch.pojo.Prediction;
import com.openclassrooms.go4lunch.pojo.Response;
import com.openclassrooms.go4lunch.pojo.Result;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PlaceStreams {

    private static final long TIME_OUT = 10;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public static Observable<Place> streamFetchNearbyPlace(Location location) {
        PlacesApi placesApi = PlacesApi.retrofit.create(PlacesApi.class);
        return placesApi.getNearbyPlaces(locationToString(location))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(TIME_OUT, TIME_UNIT);
    }

    public static Observable<PlaceDetail> streamFetchPlaceDetail(String placeId) {
        PlacesApi placesApi = PlacesApi.retrofit.create(PlacesApi.class);
        return placesApi.getPlaceDetail(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(TIME_OUT, TIME_UNIT);
    }

    public static Observable<Response> streamFetchPredictions(Location location, String input) {
        PlacesApi placesApi = PlacesApi.retrofit.create(PlacesApi.class);
        return placesApi.getPredictions(locationToString(location), input)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(TIME_OUT, TIME_UNIT);
    }

    public static Single<List<Result>> streamFetchPlaceDetailsOfPredictions(Location location, String input) {
        return streamFetchPredictions(location, input)
                .flatMapIterable(new Function<Response, List<Prediction>>() {
                    final List<Prediction> mPredictions = new ArrayList<>();
                    @Override
                    public List<Prediction> apply(@NotNull Response response) throws Exception {
                        for (Prediction prediction : response.getPredictions()) {
                            if (prediction.getTypes().contains("restaurant")) {
                                mPredictions.add(prediction);
                            }
                        }
                        return mPredictions;
                    }
                })
                .flatMap((Function<Prediction, ObservableSource<PlaceDetail>>) prediction ->
                        streamFetchPlaceDetail(prediction.getPlaceId())
                )
                .map(placeDetail -> {
                    Result mResult = new Result();
                    mResult.setPhotos(placeDetail.getResult().getPhotos());
                    mResult.setName(placeDetail.getResult().getName());
                    mResult.setVicinity(placeDetail.getResult().getVicinity());
                    mResult.setOpeningHours(placeDetail.getResult().getOpeningHours());
                    mResult.setPlaceId(placeDetail.getResult().getPlaceId());
                    mResult.setGeometry(placeDetail.getResult().getGeometry());
                    mResult.setRating(placeDetail.getResult().getRating());
                    return mResult;
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(TIME_OUT, TIME_UNIT);
    }

    private static String locationToString(Location location) {
        return location.getLatitude() + "," + location.getLongitude();
    }

}