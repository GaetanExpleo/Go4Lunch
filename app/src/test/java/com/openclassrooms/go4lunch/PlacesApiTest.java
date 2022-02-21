package com.openclassrooms.go4lunch;

import com.openclassrooms.go4lunch.pojo.Prediction;
import com.openclassrooms.go4lunch.pojo.Result;
import com.openclassrooms.go4lunch.pojo.ResultDetail;
import com.openclassrooms.go4lunch.utils.PlacesApi;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.openclassrooms.go4lunch.utils.TestUtil.TEST_RESTAURANT_ID;
import static com.openclassrooms.go4lunch.utils.TestUtil.TEST_LATITUDE;
import static com.openclassrooms.go4lunch.utils.TestUtil.TEST_LONGITUDE;
import static com.openclassrooms.go4lunch.utils.TestUtil.convertStreamToString;
import static org.junit.Assert.assertEquals;

public class PlacesApiTest {

    public MockWebServer mMockWebServer = new MockWebServer();
    private final PlacesApi mApiNearbyPlace = new Retrofit.Builder()
            .baseUrl(mMockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(PlacesApi.class);

    private final String LOCATION = TEST_LATITUDE + "," + TEST_LONGITUDE;

    @After
    public void tearDown() throws Exception {
        mMockWebServer.shutdown();
    }

    @Test
    public void getNearbyPlace_noPlace() throws IOException {
        String body = convertStreamToString(getClass().getClassLoader().getResourceAsStream("nearbyPlace_0_result.json"));
        MockResponse response = new MockResponse().setResponseCode(200).setBody(body);

        mMockWebServer.enqueue(response);

        List<Result> results = mApiNearbyPlace.getNearbyPlaces(LOCATION).blockingFirst().getResults();

        assertEquals(0, results.size());

    }

    @Test
    public void getNearbyPlace_onePlace() throws IOException {
        String body = convertStreamToString(getClass().getClassLoader().getResourceAsStream("nearbyPlace_1_result.json"));
        MockResponse response = new MockResponse().setResponseCode(200).setBody(body);

        mMockWebServer.enqueue(response);

        List<Result> results = mApiNearbyPlace.getNearbyPlaces(LOCATION).blockingFirst().getResults();

        assertEquals(1, results.size());
        Result result = results.get(0);
        assertEquals("Best Western Paris Saint-Quentin", result.getName());
    }

    @Test
    public void getNearbyPlace_20Places() throws IOException {
        String body = convertStreamToString(getClass().getClassLoader().getResourceAsStream("nearbyPlace_20_results.json"));
        MockResponse response = new MockResponse().setResponseCode(200).setBody(body);

        mMockWebServer.enqueue(response);

        List<Result> results = mApiNearbyPlace.getNearbyPlaces(LOCATION).blockingFirst().getResults();

        assertEquals(20, results.size());
        Result secondResult = results.get(1);
        Result lastResult = results.get(19);
        assertEquals("UGC", secondResult.getName());
        assertEquals("Total Petrol Station Access", lastResult.getName());
    }

    @Test
    public void getPlaceDetail_la_delizia() throws IOException {
        String body = convertStreamToString(getClass().getClassLoader().getResourceAsStream("placeDetail_laDelizia.json"));
        MockResponse response = new MockResponse().setResponseCode(200).setBody(body);

        mMockWebServer.enqueue(response);

        ResultDetail resultDetail = mApiNearbyPlace.getPlaceDetail(TEST_RESTAURANT_ID).blockingFirst().getResult();

        assertEquals("Pizzeria-Guyancourt - La Delizia", resultDetail.getName());
    }

    @Test
    public void getPredictions() throws IOException {
        String body = convertStreamToString(getClass().getClassLoader().getResourceAsStream("predictions.json"));
        MockResponse response = new MockResponse().setResponseCode(200).setBody(body);

        mMockWebServer.enqueue(response);

        List<Prediction> predictions = mApiNearbyPlace.getPredictions(LOCATION, "sush").blockingFirst().getPredictions();

        assertEquals(5, predictions.size());
        Prediction prediction = predictions.get(0);
        assertEquals("Sushi Lin", prediction.getDescription().split(",")[0]);
    }
}