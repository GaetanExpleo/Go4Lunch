package com.openclassrooms.go4lunch.utils;

import com.openclassrooms.go4lunch.BuildConfig;
import com.openclassrooms.go4lunch.pojo.Place;
import com.openclassrooms.go4lunch.pojo.PlaceDetail;
import com.openclassrooms.go4lunch.pojo.Response;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface PlacesApi {

    String key = BuildConfig.google_map_key;

    @GET("autocomplete/json?radius=1000&types=establishment&key=" + key)
    Observable<Response> getPredictions(@Query("location") String location, @Query("input") String input);

    @GET("nearbysearch/json?radius=1000&type=restaurant&key=" + key)
    Observable<Place> getNearbyPlaces(@Query("location") String location);

    @GET("details/json?key=" + key)
    Observable<PlaceDetail> getPlaceDetail(@Query("place_id") String placeId);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
}