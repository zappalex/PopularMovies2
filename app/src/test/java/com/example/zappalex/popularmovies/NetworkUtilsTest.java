package com.example.zappalex.popularmovies;

import com.example.zappalex.popularmovies.utilities.NetworkUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.URL;

import static junit.framework.Assert.assertEquals;

/**
 * Created by user on 9/30/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NetworkUtilsTest {

    private static final String TMDB_URL_SINGLE_ENDPOINT_POPULAR= "http://api.themoviedb.org/3/movie/popular?api_key=";

    @Test
    public void testTmdbUrlSingleEndpoint() {
        try {
            URL testUrl = new URL(TMDB_URL_SINGLE_ENDPOINT_POPULAR);
            URL createdUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(NetworkUtils.ENDPOINT_POPULAR_MOVIES);
            assertEquals(testUrl, createdUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
