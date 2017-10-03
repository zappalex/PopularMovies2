package com.example.zappalex.popularmovies;

import android.net.Network;
import android.net.Uri;

import com.example.zappalex.popularmovies.utilities.NetworkUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.net.URL;
import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class NetworkUtilsTest {

    // TODO : centralize api key
    private static final String TEST_ENDPOINT = "test_endpoint";
    private static final String VIDEO_KEY = "test_video_key123456";
    private static final String VIDEO_KEY_PARAM = "?v=";
    private static final String API_KEY_PARAM = "?api_key=";
    private static final String API_KEY = NetworkUtils.getApiKey();
    private static final String TMDB_BASE_URL = NetworkUtils.getBaseMovieUrl() + "/";
    private static final String TMDB_POPULAR_ENDPOINT = NetworkUtils.getEndpointPopularMovies();
    private static final String BASE_YOUTUBE_URL = NetworkUtils.getBaseYoutubeUrl();

    private static final String TMDB_URL_SINGLE_ENDPOINT_POPULAR = TMDB_BASE_URL + TMDB_POPULAR_ENDPOINT + API_KEY_PARAM + API_KEY;
    private static final String TMDB_URL_PATH_ENDPOINT = TMDB_BASE_URL + TEST_ENDPOINT + API_KEY_PARAM + API_KEY;
    private static final String YOUTUBE_URL_WITH_KEY = BASE_YOUTUBE_URL + VIDEO_KEY_PARAM + VIDEO_KEY;

    @Test
    public void testTmdbUrlSingleEndpoint() {
        try {
            URL expectedUrl = new URL(TMDB_URL_SINGLE_ENDPOINT_POPULAR);
            URL actualUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(NetworkUtils.ENDPOINT_POPULAR_MOVIES);
            assertEquals(expectedUrl, actualUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testTmdbUrlPathEndpoint(){
        try{
            URL expectedUrl = new URL(TMDB_URL_PATH_ENDPOINT);
            URL actualUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(TEST_ENDPOINT);
            assertEquals(expectedUrl, actualUrl);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testYoutubeUriWithVideoKey() {
        Uri expectedUri = Uri.parse(YOUTUBE_URL_WITH_KEY);
        Uri actualUri = NetworkUtils.buildYouTubeUriWithVideoKey(VIDEO_KEY);

        assertEquals(expectedUri, actualUri);
    }

    


}
