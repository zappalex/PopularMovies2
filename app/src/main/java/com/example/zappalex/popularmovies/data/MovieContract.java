package com.example.zappalex.popularmovies.data;

import android.net.Uri;

public class MovieContract {

    public static final String AUTHORITY = "com.example.zappalex.popularmovies";
    public static final String ENDPOINT_FAVORITE_MOVIES = "favorites";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY );

    public static final class FavoriteMovieEntry {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(ENDPOINT_FAVORITE_MOVIES).build();

        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_USER_RATING = "userRating";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
    }
}
