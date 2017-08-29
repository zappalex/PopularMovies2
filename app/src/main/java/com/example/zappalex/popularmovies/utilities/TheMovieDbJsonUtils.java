package com.example.zappalex.popularmovies.utilities;

import com.example.zappalex.popularmovies.models.Movie;
import com.example.zappalex.popularmovies.models.Review;
import com.example.zappalex.popularmovies.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 6/14/17.
 * This is a util class for parsing the json data returned from the API into our Movie object.
 */

public class TheMovieDbJsonUtils {

    // The Movie Database (TMDB) json keys
    private static final String TMDB_RESULTS = "results";
    private static final String TMDB_ID = "id";
    private static final String TMDB_TITLE = "title";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_OVERVIEW = "overview";
    private static final String TMDB_USER_RATING = "vote_average";
    private static final String TMDB_RELEASE_DATE = "release_date";
    private static final String TMDB_VIDEO_KEY = "key";
    private static final String TMDB_VIDEO_NAME = "name";
    private static final String TMDB_VIDEO_SITE = "site";
    private static final String TMDB_VIDEO_TYPE = "type";
    private static final String TMDB_REVIEW_AUTHOR = "author";
    private static final String TMDB_REVIEW_CONTENT = "content";
    private static final String TMDB_REVIEW_URL = "url";


    public static ArrayList<Movie> getMovieListFromJsonString(String jsonMovieString) throws JSONException {
        ArrayList<Movie> movieList = new ArrayList<>();
        JSONObject jsonMoviesObject = new JSONObject(jsonMovieString);

        JSONArray moviesArray = jsonMoviesObject.getJSONArray(TMDB_RESULTS);
        for(int i=0; i<moviesArray.length(); i++){
            Movie currentMovie = new Movie();
            JSONObject jsonMovie = moviesArray.getJSONObject(i);

            currentMovie.setId(jsonMovie.getString(TMDB_ID));
            currentMovie.setTitle(jsonMovie.getString(TMDB_TITLE));
            currentMovie.setPosterPath(jsonMovie.getString(TMDB_POSTER_PATH));
            currentMovie.setOverview(jsonMovie.getString(TMDB_OVERVIEW));
            currentMovie.setUserRating(jsonMovie.getString(TMDB_USER_RATING));
            currentMovie.setReleaseDate(jsonMovie.getString(TMDB_RELEASE_DATE));

            movieList.add(currentMovie);
        }
        return movieList;
    }

    public static ArrayList<Video> getVideoListFromJsonString (String jsonVideosString) throws JSONException {
        ArrayList<Video> videosList = new ArrayList<>();
        JSONObject jsonVideosObject = new JSONObject(jsonVideosString);

        JSONArray videosArray = jsonVideosObject.getJSONArray(TMDB_RESULTS);
        for ( int i=0; i<videosArray.length(); i++){
            Video currentVideo = new Video();
            JSONObject jsonVideo = videosArray.getJSONObject(i);

            currentVideo.setId(jsonVideo.getString(TMDB_ID));
            currentVideo.setKey(jsonVideo.getString(TMDB_VIDEO_KEY));
            currentVideo.setName(jsonVideo.getString(TMDB_VIDEO_NAME));
            currentVideo.setSite(jsonVideo.getString(TMDB_VIDEO_SITE));
            currentVideo.setType(jsonVideo.getString(TMDB_VIDEO_TYPE));

            videosList.add(currentVideo);
        }
        return videosList;
    }

    public static ArrayList<Review> getReviewListFromJsonString (String jsonReviewsString) throws JSONException{
        ArrayList<Review> reviewsList = new ArrayList<>();
        JSONObject jsonReviewsObject = new JSONObject(jsonReviewsString);

        JSONArray reviewsArray = jsonReviewsObject.getJSONArray(TMDB_RESULTS);
        for(int i=0; i<reviewsArray.length(); i++){
            Review currentReview = new Review();
            JSONObject jsonReview = reviewsArray.getJSONObject(i);

            currentReview.setId(jsonReview.getString(TMDB_ID));
            currentReview.setAuthor(jsonReview.getString(TMDB_REVIEW_AUTHOR));
            currentReview.setContent(jsonReview.getString(TMDB_REVIEW_CONTENT));
            currentReview.setUrl(jsonReview.getString(TMDB_REVIEW_URL));

            reviewsList.add(currentReview);
        }
        return reviewsList;
    }

}
