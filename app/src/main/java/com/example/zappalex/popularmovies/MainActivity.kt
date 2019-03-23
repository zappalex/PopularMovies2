package com.example.zappalex.popularmovies


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.example.zappalex.popularmovies.adapters.MovieAdapter
import com.example.zappalex.popularmovies.data.MovieContract
import com.example.zappalex.popularmovies.models.Movie
import com.example.zappalex.popularmovies.utilities.FormatUtils
import com.example.zappalex.popularmovies.utilities.NetworkUtils
import com.example.zappalex.popularmovies.utilities.TheMovieDbJsonUtils

import java.net.URL
import java.util.ArrayList

class MainActivity : AppCompatActivity(), MovieAdapter.MovieAdapterOnClickHandler {

    private var mLoaderManager: LoaderManager? = null
    private var mMovieRecyclerView: RecyclerView? = null
    private var mGridLayoutManager: GridLayoutManager? = null
    private var mMovieAdapter: MovieAdapter? = null
    private var mMovieSortOrder = DEFAULT_SORT_ORDER

    private var mScrollIndex = 0
    private var mScrollOffset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLoaderManager = supportLoaderManager

        mMovieRecyclerView = findViewById(R.id.rv_movie_list) as RecyclerView
        mGridLayoutManager = initializeGridLayoutManager()
        mMovieRecyclerView!!.layoutManager = mGridLayoutManager

        mMovieAdapter = MovieAdapter(this)
        mMovieRecyclerView!!.adapter = mMovieAdapter

    }

    // in portrait, grid will have 2 columns and in landscape grid will have 3.
    private fun initializeGridLayoutManager(): GridLayoutManager {
        val gridLayoutManager: GridLayoutManager
        val deviceOrientation = FormatUtils.getDeviceOrientation(this)

        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = GridLayoutManager(this, GRID_LAYOUT_SPAN_PORTRAIT, GridLayoutManager.VERTICAL, false)
        } else {
            gridLayoutManager = GridLayoutManager(this, GRID_LAYOUT_SPAN_LANDSCAPE, GridLayoutManager.VERTICAL, false)
        }
        return gridLayoutManager
    }

    override fun onPause() {
        super.onPause()
        saveScrollInfo()
    }

    private fun saveScrollInfo() {
        mScrollIndex = mGridLayoutManager!!.findFirstVisibleItemPosition()
        val startView = mMovieRecyclerView!!.getChildAt(0)
        mScrollOffset = if (startView ==
                null) 0 else startView.top - mMovieRecyclerView!!.paddingTop
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_SCROLL_INDEX, mScrollIndex)
        outState.putInt(EXTRA_SCROLL_OFFSET, mScrollOffset)
        outState.putString(EXTRA_SORT_ORDER, mMovieSortOrder)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mScrollIndex = savedInstanceState.getInt(EXTRA_SCROLL_INDEX)
        mScrollOffset = savedInstanceState.getInt(EXTRA_SCROLL_OFFSET)
        mMovieSortOrder = savedInstanceState.getString(EXTRA_SORT_ORDER, DEFAULT_SORT_ORDER)
    }

    override fun onResume() {
        super.onResume()
        fetchMovies(mMovieSortOrder)
    }

    private fun fetchMovies(urlEndpoint: String) {

        when (urlEndpoint) {
            NetworkUtils.ENDPOINT_POPULAR_MOVIES -> fetchMoviesOnlyIfDeviceOnline(urlEndpoint)
            NetworkUtils.ENDPOINT_TOP_RATED_MOVIES -> fetchMoviesOnlyIfDeviceOnline(urlEndpoint)
            MovieContract.ENDPOINT_FAVORITE_MOVIES -> fetchFavoriteMovies()
        }
    }

    private fun fetchMoviesOnlyIfDeviceOnline(urlEndpoint: String) {
        if (NetworkUtils.isDeviceOnline(this)) {
            val queryBundle = Bundle()
            queryBundle.putString(QUERY_TMDB_MOVIE_BUNDLE_EXTRA, urlEndpoint)

            mLoaderManager!!.restartLoader(TMDB_MOVIE_LOADER_ID, queryBundle, TmdbMoviesCallback())

        } else {
            Toast.makeText(this, getString(R.string.msg_movies_offline), Toast.LENGTH_SHORT).show()
        }
    }

    private inner class TmdbMoviesCallback : LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

        override fun onCreateLoader(id: Int, args: Bundle): android.support.v4.content.Loader<ArrayList<Movie>> {
            return object : AsyncTaskLoader<ArrayList<Movie>>(baseContext) {

                override fun onStartLoading() {
                    super.onStartLoading()
                    forceLoad()
                }

                override fun loadInBackground(): ArrayList<Movie>? {
                    val endpoint = args.getString(QUERY_TMDB_MOVIE_BUNDLE_EXTRA)
                    val movieRequestUrl = NetworkUtils.buildTmdbUrlWithSingleEndpoint(endpoint)

                    if (movieRequestUrl != null) {
                        try {
                            val jsonMovieString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl)
                            return TheMovieDbJsonUtils.getMovieListFromJsonString(jsonMovieString)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    return null
                }
            }
        }

        override fun onLoadFinished(loader: android.support.v4.content.Loader<ArrayList<Movie>>, movieList: ArrayList<Movie>) {
            displayMoviesInGridLayout(movieList)
        }

        override fun onLoaderReset(loader: Loader<ArrayList<Movie>>) {}
    }

    private fun displayMoviesInGridLayout(moviesList: ArrayList<Movie>?) {
        if (moviesList != null) {
            mMovieAdapter!!.setMovieList(moviesList)
            scrollToSavedPositionWithOffset()
        } else {
            Toast.makeText(this, getString(R.string.msg_movie_service_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun scrollToSavedPositionWithOffset() {
        if (mScrollIndex != -1) {
            mGridLayoutManager!!.scrollToPositionWithOffset(mScrollIndex, mScrollOffset)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_sort_popular) {
            fetchMoviesOnlyIfDeviceOnline(NetworkUtils.ENDPOINT_POPULAR_MOVIES)
            mMovieSortOrder = NetworkUtils.ENDPOINT_POPULAR_MOVIES
            return true
        } else if (id == R.id.action_sort_top_rated) {
            fetchMoviesOnlyIfDeviceOnline(NetworkUtils.ENDPOINT_TOP_RATED_MOVIES)
            mMovieSortOrder = NetworkUtils.ENDPOINT_TOP_RATED_MOVIES
            return true
        } else if (id == R.id.action_favorites) {
            mMovieSortOrder = MovieContract.ENDPOINT_FAVORITE_MOVIES
            fetchFavoriteMovies()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fetchFavoriteMovies() {
        val queryBundle = Bundle()
        queryBundle.putString(QUERY_FAVORITE_MOVIES_BUNDLE_EXTRA, null)

        mLoaderManager!!.restartLoader(FAVORITE_MOVIES_LOADER_ID, queryBundle, FavoriteMoviesCallback())
    }

    private inner class FavoriteMoviesCallback : LoaderManager.LoaderCallbacks<Cursor> {

        override fun onCreateLoader(id: Int, args: Bundle): Loader<Cursor> {
            return object : AsyncTaskLoader<Cursor>(baseContext) {

                override fun onStartLoading() {
                    super.onStartLoading()
                    forceLoad()
                }

                override fun loadInBackground(): Cursor? {
                    try {
                        return contentResolver.query(MovieContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return null
                    }

                }
            }
        }

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
            displayMoviesInGridLayout(extractFavoriteMoviesFromCursor(data))
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}
    }

    private fun extractFavoriteMoviesFromCursor(favoritesCursor: Cursor): ArrayList<Movie> {
        val movieList = ArrayList<Movie>()
        try {
            while (favoritesCursor.moveToNext()) {
                val id = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_ID))
                val title = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_TITLE))
                val posterPath = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH))
                val overview = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW))
                val userRating = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_USER_RATING))
                val releaseDate = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE))

                val currentMovie = Movie(id, title, posterPath, overview, userRating, releaseDate)
                movieList.add(currentMovie)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            favoritesCursor.close()
        }
        return movieList
    }

    override fun onClick(movie: Movie) {
        destroyLoaders()

        val context = this
        val destinationActivity = MovieDetailActivity::class.java
        val intentStartMovieDetail = Intent(context, destinationActivity)
        intentStartMovieDetail.putExtra(PARCELABLE_MOVIE, movie)
        startActivity(intentStartMovieDetail)
    }

    // Loaders must be destroyed when going to child activity, or they will both automatically be called upon return.
    private fun destroyLoaders() {
        mLoaderManager!!.destroyLoader(TMDB_MOVIE_LOADER_ID)
        mLoaderManager!!.destroyLoader(FAVORITE_MOVIES_LOADER_ID)
    }

    companion object {

        val PARCELABLE_MOVIE = "parcelable_movie"

        private val GRID_LAYOUT_SPAN_PORTRAIT = 2
        private val GRID_LAYOUT_SPAN_LANDSCAPE = 3

        private val EXTRA_SCROLL_INDEX = "scroll_index"
        private val EXTRA_SCROLL_OFFSET = "scroll_offset"
        private val EXTRA_SORT_ORDER = "sort_order"
        private val DEFAULT_SORT_ORDER = NetworkUtils.ENDPOINT_POPULAR_MOVIES

        private val QUERY_TMDB_MOVIE_BUNDLE_EXTRA = "tmdb_movie_query"
        private val TMDB_MOVIE_LOADER_ID = 455
        private val QUERY_FAVORITE_MOVIES_BUNDLE_EXTRA = "favorite_movies"
        private val FAVORITE_MOVIES_LOADER_ID = 456
    }

}
