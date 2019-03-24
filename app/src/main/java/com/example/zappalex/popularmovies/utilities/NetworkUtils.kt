package com.example.zappalex.popularmovies.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri

import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner

/**
 * Created by user on 6/14/17.
 * This is a util class that handles operations related to networking and
 * fetching information from the API
 */

class NetworkUtils {
    companion object {
        // Keys - add API key here
        val apiKey = ""

        // URL Paths
        val baseMovieUrl = "http://api.themoviedb.org/3/movie"
        val basePicassoUrl = "http://image.tmdb.org/t/p/"
        val baseYoutubeUrl = "https://www.youtube.com/watch"
        val endpointPopularMovies = "popular"
        val endpointTopRatedMovies = "top_rated"
        val endpointVideos = "videos"
        val endpointReviews = "reviews"

        // Query Paths
        val apiKeyParam = "api_key"
        val youtubeVideoKeyParam = "v"

        // Picasso sizes ( only one for now )
        val imgSizeW342 = "w342"

        // this will create a url by encoding and then appending a single string endpoint
        fun buildTmdbUrlWithSingleEndpoint(endpoint: String): URL? {
            val builtUri = Uri.parse(baseMovieUrl).buildUpon()
                    .appendPath(endpoint)
                    .appendQueryParameter(apiKeyParam, apiKey)
                    .build()

            return convertUriToUrl(builtUri)
        }

        // this will create a url by simply adding the encoded path to the base url
        fun buildTmdbUrlWithPathEndpoint(pathEndpoint: String): URL? {
            val builtUri = Uri.parse(baseMovieUrl).buildUpon()
                    .appendEncodedPath(pathEndpoint)
                    .appendQueryParameter(apiKeyParam, apiKey)
                    .build()
            return convertUriToUrl(builtUri)
        }

        fun buildYouTubeUriWithVideoKey(videoKey: String): Uri {
            return Uri.parse(baseYoutubeUrl).buildUpon()
                    .appendQueryParameter(youtubeVideoKeyParam, videoKey)
                    .build()
        }

        fun formatVideosEndpointWithId(id: String?): String {
            return "$id/$endpointVideos"
        }

        fun formatReviewsEndpointWithId(id: String?): String {
            return "$id/$endpointReviews"
        }

        // build uri then return correct url
        private fun convertUriToUrl(uriToConvert: Uri): URL? {
            var url: URL? = null
            try {
                url = URL(uriToConvert.toString())
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

            return url
        }

        // return a formatted picasso url
        fun buildPicassoUrl(posterPath: String): String {
            return "$basePicassoUrl$imgSizeW342/$posterPath"
        }

        @Throws(IOException::class)
        fun getResponseFromHttpUrl(url: URL?): String? {
            val urlConnection = url?.openConnection() as HttpURLConnection
            try {
                val `in` = urlConnection.inputStream

                val scanner = Scanner(`in`)
                scanner.useDelimiter("\\A")

                val hasInput = scanner.hasNext()
                return if (hasInput) {
                    scanner.next()
                } else {
                    null
                }
            } finally {
                urlConnection.disconnect()
            }
        }

        // check if device is online
        fun isDeviceOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo.isConnectedOrConnecting
        }
    }
}
