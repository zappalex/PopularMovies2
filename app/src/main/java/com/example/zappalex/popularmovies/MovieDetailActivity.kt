package com.example.zappalex.popularmovies

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast

import com.example.zappalex.popularmovies.adapters.ReviewAdapter
import com.example.zappalex.popularmovies.adapters.VideoAdapter
import com.example.zappalex.popularmovies.data.MovieContract
import com.example.zappalex.popularmovies.models.Movie
import com.example.zappalex.popularmovies.models.Review
import com.example.zappalex.popularmovies.models.Video
import com.example.zappalex.popularmovies.utilities.FormatUtils
import com.example.zappalex.popularmovies.utilities.NetworkUtils
import com.example.zappalex.popularmovies.utilities.TheMovieDbJsonUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.video_list_item.*

import java.util.ArrayList


class MovieDetailActivity : AppCompatActivity(), VideoAdapter.VideoAdapterOnClickHandler {

    private var currentMovie: Movie? = null
    private var isMovieInFavorites = false

    private var videoList: ArrayList<Video> = arrayListOf<Video>()
    private val videoAdapter = VideoAdapter(this)
    private val reviewAdapter = ReviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        initLayoutAdapters()
        retrieveIntents()

        isMovieInFavorites = isMovieInFavorites(queryCurrentMovie(), currentMovie)
        toggleFavoritesHeartImage(isMovieInFavorites)

        favoriteImageView.setOnClickListener { handleFavoriteClickLogic() }
    }

    private fun initLayoutAdapters() {

        val videosLinearLayoutManager = LinearLayoutManager(this)
        videosRecyclerView.layoutManager = videosLinearLayoutManager
        videosRecyclerView.adapter = videoAdapter

        val reviewsLinearLayoutManager = LinearLayoutManager(this)
        reviewsRecyclerView.layoutManager = reviewsLinearLayoutManager
        reviewsRecyclerView.adapter = reviewAdapter

        videosRecyclerView.isNestedScrollingEnabled = false
        reviewsRecyclerView.isNestedScrollingEnabled = false
    }

    private fun retrieveIntents() {
        val intentThatStartedActivity = intent
        if (intentThatStartedActivity.hasExtra(MainActivity.PARCELABLE_MOVIE)) {
            currentMovie = intentThatStartedActivity.getParcelableExtra(MainActivity.PARCELABLE_MOVIE)
            currentMovie?.let { populateViews(it) }

            val videosEndpoint = NetworkUtils.formatVideosEndpointWithId(currentMovie?.id)
            fetchVideosOnlyIfOnline(videosEndpoint)

            val reviewsEndpoint = NetworkUtils.formatReviewsEndpointWithId(currentMovie?.id)
            fetchReviewsOnlyIfOnline(reviewsEndpoint)

        } else {
            Toast.makeText(this, getString(R.string.msg_movie_detail_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun populateViews(movie: Movie) {
        movieTitleTextView.text = movie.title
        movieDateTextView.text = FormatUtils.getYearFromDateString(movie.releaseDate)
        ratingTextView.text = FormatUtils.getFormattedRating(movie.userRating)
        movieOverviewTextView.text = movie.overview

        val picassoImgUrl = NetworkUtils.buildPicassoUrl(movie.posterPath)
        Picasso.with(this).load(picassoImgUrl).into(moviePosterImg)
    }

    private fun fetchVideosOnlyIfOnline(videosEndpoint: String) {
        if (NetworkUtils.isDeviceOnline(this)) {
            val queryBundle = Bundle()
            queryBundle.putString(VIDEOS_QUERY_BUNDLE_EXTRA, videosEndpoint)

            val loaderManager = supportLoaderManager
            loaderManager.initLoader(VIDEOS_LOADER_ID, queryBundle, VideosCallback())

        } else {
            Toast.makeText(this, getString(R.string.msg_videos_offline), Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchReviewsOnlyIfOnline(reviewsEndpoint: String) {
        if (NetworkUtils.isDeviceOnline(this)) {
            val queryBundle = Bundle()
            queryBundle.putString(REVIEWS_QUERY_BUNDLE_EXTRA, reviewsEndpoint)

            val loaderManager = supportLoaderManager
            loaderManager.initLoader(REVIEWS_LOADER_ID, queryBundle, ReviewsCallback())
        } else {
            Toast.makeText(this, getString(R.string.msg_reviews_offline), Toast.LENGTH_LONG).show()
        }
    }

    private inner class VideosCallback : LoaderManager.LoaderCallbacks<ArrayList<Video>> {
        override fun onCreateLoader(id: Int, args: Bundle): Loader<ArrayList<Video>> {
            val videosEndpoint = args.getString(VIDEOS_QUERY_BUNDLE_EXTRA)
            return VideoAsyncTaskLoader(this@MovieDetailActivity, videosEndpoint)
        }

        override fun onLoadFinished(loader: Loader<ArrayList<Video>>, data: ArrayList<Video>) {
            videoList = data
            videoAdapter.setVideoList(data)
        }

        override fun onLoaderReset(loader: Loader<ArrayList<Video>>) {}
    }

    private class VideoAsyncTaskLoader(context: Context, val videosEndpoint: String) : AsyncTaskLoader<ArrayList<Video>>(context) {
        private var cachedVideos= arrayListOf<Video>()

        override fun onStartLoading() {
            if (!cachedVideos.isEmpty()) {
                deliverResult(cachedVideos)
            } else {
                forceLoad()
            }
        }

        override fun loadInBackground(): ArrayList<Video> {
            val videosRequestUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(videosEndpoint)
            videosRequestUrl?.let {
                try {
                    val jsonVideosString = NetworkUtils.getResponseFromHttpUrl(it)
                    return TheMovieDbJsonUtils.getVideoListFromJsonString(jsonVideosString)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return arrayListOf<Video>()
        }

        override fun deliverResult(data: ArrayList<Video>) {
            cachedVideos = data
            super.deliverResult(data)
        }
    }

    private inner class ReviewsCallback : LoaderManager.LoaderCallbacks<ArrayList<Review>> {
        override fun onCreateLoader(id: Int, args: Bundle): Loader<ArrayList<Review>> {
            val reviewsEndpoint = args.getString(REVIEWS_QUERY_BUNDLE_EXTRA)
            return ReviewsAsyncTaskLoader(this@MovieDetailActivity, reviewsEndpoint)
        }

        override fun onLoadFinished(loader: Loader<ArrayList<Review>>, data: ArrayList<Review>) {
            reviewAdapter.setReviewList(data)
        }

        override fun onLoaderReset(loader: Loader<ArrayList<Review>>) {}
    }

    private class ReviewsAsyncTaskLoader(context: Context, val reviewsEndpoint: String) : AsyncTaskLoader<ArrayList<Review>>(context) {
        private var cachedReviews= arrayListOf<Review>()

        override fun onStartLoading() {
            super.onStartLoading()

            if (!cachedReviews.isEmpty()) {
                deliverResult(cachedReviews)
            } else {
                forceLoad()
            }
        }

        override fun loadInBackground(): ArrayList<Review> {
            // TODO: fix returning null here?
            val reviewsRequestUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(reviewsEndpoint)
                try {
                    val jsonReviewsString = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl)
                    return TheMovieDbJsonUtils.getReviewListFromJsonString(jsonReviewsString)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            return arrayListOf<Review>()
        }

        override fun deliverResult(data: ArrayList<Review>) {
            super.deliverResult(data)
            cachedReviews = data
        }
    }

    override fun onCLick(position: Int) {
        if (videoList.size > 0) {
            val youtubeUri = NetworkUtils.buildYouTubeUriWithVideoKey(videoList[position].key)
            val viewVideoIntent = Intent(Intent.ACTION_VIEW, youtubeUri)

            if (viewVideoIntent.resolveActivity(packageManager) != null) {
                startActivity(viewVideoIntent)
            }
        }
    }

    private fun isMovieInFavorites(favoritesCursor: Cursor, currentMovie: Movie?): Boolean {
        try {
            while (favoritesCursor.moveToNext()) {
                val cursorMovieId = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_ID))
                if (currentMovie?.id == cursorMovieId) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            favoritesCursor.close()
        }

        return false
    }

    private fun queryCurrentMovie(): Cursor {
        return contentResolver.query(MovieContract.FavoriteMovieEntry.CONTENT_URI, null, null,
                arrayOf(currentMovie?.id), null)
    }

    private fun toggleFavoritesHeartImage(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteImageView.setImageResource(R.drawable.heart_red)
        } else {
            favoriteImageView.setImageResource(R.drawable.heart_grey)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val scrollPosition = movieDetailScrollView.scrollY
        outState.putInt(EXTRA_SCROLL_POSITION, scrollPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val scrollPosition = savedInstanceState.getInt(EXTRA_SCROLL_POSITION)
        movieDetailScrollView.scrollTo(0, scrollPosition)
    }

    fun onFavoriteClick() {
        handleFavoriteClickLogic()
    }

    private fun handleFavoriteClickLogic() {
        if (isMovieInFavorites) {
            deleteMoveFromFavoritesDb()
            isMovieInFavorites = false
        } else {
            insertMovieIntoFavoritesDb()
            isMovieInFavorites = true
        }
        toggleFavoritesHeartImage(isMovieInFavorites)
    }

    private fun deleteMoveFromFavoritesDb() {
        val idOfMovieToDelete = currentMovie?.id
        var favoriteDeleted = 0

        idOfMovieToDelete.let {
            val uriToDelete = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath(it).build()
            favoriteDeleted = contentResolver.delete(uriToDelete, null, null)
        }

        if (favoriteDeleted == 0) {
            Toast.makeText(this, getString(R.string.action_movie_deleted_from_favorites_failure), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.action_movie_deleted_from_favorites), Toast.LENGTH_SHORT).show()
        }
    }

    private fun insertMovieIntoFavoritesDb() {
        val favoriteMovieContentValues = createFavoriteMovieContentValues()
        var insertResultUri: Uri? = contentResolver.insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, favoriteMovieContentValues)

        if (insertResultUri != null) {
            displayStatusOfSuccessfulInsert(true)
        } else {
            displayStatusOfSuccessfulInsert(false)
        }
    }

    private fun createFavoriteMovieContentValues(): ContentValues? {
            val cv = ContentValues()
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_ID, currentMovie?.id)
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, currentMovie?.title)
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, currentMovie?.posterPath)
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, currentMovie?.overview)
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_USER_RATING, currentMovie?.userRating)
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, currentMovie?.releaseDate)
            return cv

    }

    private fun displayStatusOfSuccessfulInsert(isInsertSuccessful: Boolean) {
        if (isInsertSuccessful) {
            Toast.makeText(this, getString(R.string.action_movie_added_to_favorites_success), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.action_movie_added_to_favorites_failure), Toast.LENGTH_LONG).show()
        }
    }

    companion object {

        private val EXTRA_SCROLL_POSITION = "scroll_position"
        private val VIDEOS_QUERY_BUNDLE_EXTRA = "videos_query"
        private val REVIEWS_QUERY_BUNDLE_EXTRA = "reviews_query"
        private val VIDEOS_LOADER_ID = 56
        private val REVIEWS_LOADER_ID = 57
    }

}
