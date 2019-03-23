package com.example.zappalex.popularmovies.utilities

import com.example.zappalex.popularmovies.models.Movie
import com.example.zappalex.popularmovies.models.Review
import com.example.zappalex.popularmovies.models.Video

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

/**
 * Created by user on 6/14/17.
 * This is a util class for parsing the json data returned from the API into our Movie object.
 */

class TheMovieDbJsonUtils {
    companion object {
        // The Movie Database (TMDB) json keys
        private val TMDB_RESULTS = "results"
        private val TMDB_ID = "id"
        private val TMDB_TITLE = "title"
        private val TMDB_POSTER_PATH = "poster_path"
        private val TMDB_OVERVIEW = "overview"
        private val TMDB_USER_RATING = "vote_average"
        private val TMDB_RELEASE_DATE = "release_date"
        private val TMDB_VIDEO_KEY = "key"
        private val TMDB_VIDEO_NAME = "name"
        private val TMDB_VIDEO_SITE = "site"
        private val TMDB_VIDEO_TYPE = "type"
        private val TMDB_REVIEW_AUTHOR = "author"
        private val TMDB_REVIEW_CONTENT = "content"
        private val TMDB_REVIEW_URL = "url"

        @Throws(JSONException::class)
        fun getMovieListFromJsonString(jsonMovieString: String): ArrayList<Movie> {
            val movieList = ArrayList<Movie>()
            val jsonMoviesObject = JSONObject(jsonMovieString)

            val moviesArray = jsonMoviesObject.getJSONArray(TMDB_RESULTS)
            for (i in 0 until moviesArray.length()) {
                val jsonMovie = moviesArray.getJSONObject(i)
                val currentMovie = Movie(
                        jsonMovie.getString(TMDB_ID),
                        jsonMovie.getString(TMDB_TITLE),
                        jsonMovie.getString(TMDB_POSTER_PATH),
                        jsonMovie.getString(TMDB_OVERVIEW),
                        jsonMovie.getString(TMDB_USER_RATING),
                        jsonMovie.getString(TMDB_RELEASE_DATE)
                )

                movieList.add(currentMovie)
            }
            return movieList
        }

        @Throws(JSONException::class)
        fun getVideoListFromJsonString(jsonVideosString: String): ArrayList<Video> {
            val videosList = ArrayList<Video>()
            val jsonVideosObject = JSONObject(jsonVideosString)

            val videosArray = jsonVideosObject.getJSONArray(TMDB_RESULTS)
            for (i in 0 until videosArray.length()) {
                val jsonVideo = videosArray.getJSONObject(i)

                val currentVideo = Video(
                        jsonVideo.getString(TMDB_ID),
                        jsonVideo.getString(TMDB_VIDEO_KEY),
                        jsonVideo.getString(TMDB_VIDEO_NAME),
                        jsonVideo.getString(TMDB_VIDEO_SITE),
                        jsonVideo.getString(TMDB_VIDEO_TYPE)
                )
                videosList.add(currentVideo)
            }
            return videosList
        }

        @Throws(JSONException::class)
        fun getReviewListFromJsonString(jsonReviewsString: String): ArrayList<Review> {
            val reviewsList = ArrayList<Review>()
            val jsonReviewsObject = JSONObject(jsonReviewsString)

            val reviewsArray = jsonReviewsObject.getJSONArray(TMDB_RESULTS)
            for (i in 0 until reviewsArray.length()) {
                val jsonReview = reviewsArray.getJSONObject(i)

                val currentReview = Review(
                        jsonReview.getString(TMDB_ID),
                        jsonReview.getString(TMDB_REVIEW_AUTHOR),
                        jsonReview.getString(TMDB_REVIEW_CONTENT),
                        jsonReview.getString(TMDB_REVIEW_URL)
                )
                reviewsList.add(currentReview)
            }
            return reviewsList
        }
    }
}
