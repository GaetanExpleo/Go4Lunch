package com.openclassrooms.go4lunch.ui;

import android.location.Location;
import android.location.LocationManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.go4lunch.pojo.Place;
import com.openclassrooms.go4lunch.pojo.PlaceDetail;
import com.openclassrooms.go4lunch.pojo.Result;
import com.openclassrooms.go4lunch.utils.PlaceStreams;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PlaceObservablesTest {

    private final Location TEST_LOCATION1 = new Location(LocationManager.NETWORK_PROVIDER);
    private final Location TEST_LOCATION2 = new Location(LocationManager.NETWORK_PROVIDER);

    @Before
    public void setUp() {
        TEST_LOCATION1.setLatitude(48.7875);
        TEST_LOCATION1.setLongitude(2.0462);

        TEST_LOCATION2.setLatitude(49.07053557363648);
        TEST_LOCATION2.setLongitude(0.8175356977158256);
    }

    @Test
    public void fetchNearbyPlaceTest_0Results() {
        Observable<Place> placeObservable =
                PlaceStreams.streamFetchNearbyPlace(TEST_LOCATION2);

        TestObserver<Place> testObserver = new TestObserver<>();

        placeObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        Place placeFetched = testObserver.values().get(0);

        assertThat(placeFetched.getResults().size(), is(0));
    }

    @Test
    public void fetchNearbyPlaceTest_20Results() {

        Observable<Place> placeObservable =
                PlaceStreams.streamFetchNearbyPlace(TEST_LOCATION1);

        TestObserver<Place> testObserver = new TestObserver<>();

        placeObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        Place placeFetched = testObserver.values().get(0);

        assertThat(placeFetched.getResults().size(), is(20));

        Result firstPlace = placeFetched.getResults().get(0);
        Result lastPlace = placeFetched.getResults().get(19);
        assertThat(firstPlace.getPlaceId(), is("ChIJve7xasWG5kcRGCXBGAvqDDw"));
        assertThat(lastPlace.getPlaceId(), is("ChIJdeTers-G5kcR0Tg7av2eq3Q"));

        assertThat(firstPlace.getName(), is(notNullValue()));
        assertThat(firstPlace.getVicinity(), is(notNullValue()));
        assertThat(firstPlace.getRating(), is(notNullValue()));
    }

    @Test
    public void fetchDetail_noId() {
        Observable<PlaceDetail> detailObservable =
                PlaceStreams.streamFetchPlaceDetail("");

        TestObserver<PlaceDetail> testObserver = new TestObserver<>();

        detailObservable.subscribeWith(testObserver)
                .assertNoTimeout()
                .assertNoErrors()
                .awaitTerminalEvent();

        PlaceDetail detailFetched = testObserver.values().get(0);

        assertThat(detailFetched.getResult(), is(nullValue()));

    }

    @Test
    public void fetchDetail_laDelizia() {
        Observable<PlaceDetail> detailObservable =
                PlaceStreams.streamFetchPlaceDetail("ChIJgfe3nCuH5kcREKTd0qOKTdI");

        TestObserver<PlaceDetail> testObserver = new TestObserver<>();

        detailObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        PlaceDetail detail = testObserver.values().get(0);
        assertThat(detail.getResult().getName(), is("Pizzeria-Guyancourt - La Delizia"));
    }

    @Test
    public void fetchPredictions_sush() {

        Single<List<Result>> predictionObservable =
                PlaceStreams.streamFetchPlaceDetailsOfPredictions(TEST_LOCATION1, "sush");

        TestObserver<List<Result>> testObserver = new TestObserver<>();

        predictionObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        List<Result> predictionsFetched = testObserver.values().get(0);
        assertThat(predictionsFetched.size(), is(5));

        Result prediction = predictionsFetched.get(0);
        assertThat(prediction.getName().toLowerCase(), startsWith("sush"));
    }

    @Test
    public void fetchPredictions_0Predictions() {

        Single<List<Result>> predictionObservable =
                PlaceStreams.streamFetchPlaceDetailsOfPredictions(TEST_LOCATION1, "dzszt");

        TestObserver<List<Result>> testObserver = new TestObserver<>();

        predictionObservable.subscribeWith(testObserver)
                .assertNoErrors()
                .assertNoTimeout()
                .awaitTerminalEvent();

        List<Result> predictionsFetched = testObserver.values().get(0);
        assertThat(predictionsFetched.size(), is(0));
    }
}