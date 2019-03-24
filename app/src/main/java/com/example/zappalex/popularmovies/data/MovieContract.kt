package com.example.zappalex.popularmovies.data

import android.net.Uri

object MovieContract {

    val AUTHORITY = "com.example.zappalex.popularmovies"
    val ENDPOINT_FAVORITE_MOVIES = "favorites"
    private val BASE_CONTENT_URI = Uri.parse("content://$AUTHORITY")

    object FavoriteMovieEntry {
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(ENDPOINT_FAVORITE_MOVIES).build()
        val TABLE_NAME = "favoriteMovies"
        val COLUMN_ID = "id"
        val COLUMN_TITLE = "title"
        val COLUMN_POSTER_PATH = "posterPath"
        val COLUMN_OVERVIEW = "overview"
        val COLUMN_USER_RATING = "userRating"
        val COLUMN_RELEASE_DATE = "releaseDate"
    }
}
