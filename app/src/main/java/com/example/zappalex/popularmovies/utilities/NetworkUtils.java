package com.example.zappalex.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by user on 6/14/17.
 * This is a util class that handles operations related to networking and
 * fetching information from the API
 */

public class NetworkUtils {

    // Keys - add API key here
    private static final String API_KEY = "";

    // URL Paths
    private static final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie";
    private static final String BASE_PICASSO_URL = "http://image.tmdb.org/t/p/";
    private static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch";
    public static final String ENDPOINT_POPULAR_MOVIES = "popular";
    public static final String ENDPOINT_TOP_RATED_MOVIES = "top_rated";
    private static final String ENDPOINT_VIDEOS = "videos";
    private static final String ENDPOINT_REVIEWS = "reviews";

    // Query Paths
    private static final String API_KEY_PARAM = "api_key";
    private static final String YOUTUBE_VIDEO_KEY_PARAM = "v";

    // Picasso sizes ( only one for now )
    private static final String IMG_SIZE_W342 = "w342";

    // this will create a url by encoding and then appending a single string endpoint
    public static URL buildTmdbUrlWithSingleEndpoint(String endpoint) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(endpoint)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        return  convertUriToUrl(builtUri);
    }

    // this will create a url by simply adding the encoded path to the base url
    public static URL buildTmdbUrlWithPathEndpoint(String pathEndpoint) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendEncodedPath(pathEndpoint)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        return  convertUriToUrl(builtUri);
    }

    public static Uri buildYouTubeUriWithVideoKey(String videoKey){
         return Uri.parse(BASE_YOUTUBE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_KEY_PARAM, videoKey)
                .build();
    }

    public static String formatVideosEndpointWithId(String id){
        return id + "/" + ENDPOINT_VIDEOS ;
    }

    public static String formatReviewsEndpointWithId(String id){
        return id + "/" + ENDPOINT_REVIEWS;
    }

    // build uri then return correct url
    private static URL convertUriToUrl(Uri uriToConvert) {
        URL url = null;
        try {
            url = new URL(uriToConvert.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // return a formatted picasso url
    public static String buildPicassoUrl(String posterPath) {
        return BASE_PICASSO_URL + IMG_SIZE_W342 + "/" + posterPath;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // check if device is online
    public static boolean isDeviceOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
