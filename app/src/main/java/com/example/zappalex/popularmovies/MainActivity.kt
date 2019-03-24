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
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.example.zappalex.popularmovies.adapters.MovieAdapter
import com.example.zappalex.popularmovies.data.MovieContract
import com.example.zappalex.popularmovies.models.Movie
import com.example.zappalex.popularmovies.utilities.FormatUtils
import com.example.zappalex.popularmovies.utilities.NetworkUtils
import com.example.zappalex.popularmovies.utilities.TheMovieDbJsonUtils
import kotlinx.android.synthetic.main.activity_main.*

import java.util.ArrayList

class MainActivity : AppCompatActivity(), MovieAdapter.MovieAdapterOnClickHandler {

    private val loaderManager = supportLoaderManager
    private var gridLayoutManager: GridLayoutManager? = null
    private var movieAdapter: MovieAdapter? = null
    private var movieSortOrder = DEFAULT_SORT_ORDER

    private var scrollIndex = 0
    private var scrollOffset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayoutManager = initializeGridLayoutManager()
        movieAdapter = MovieAdapter(this)
        movieRecyclerView.layoutManager = gridLayoutManager
        movieRecyclerView.adapter = movieAdapter
    }

    // in portrait, grid will have 2 columns and in landscape grid will have 3.
    private fun initializeGridLayoutManager(): GridLayoutManager {
        var gridLayoutManager: GridLayoutManager
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
        gridLayoutManager?.let { scrollIndex = it.findFirstVisibleItemPosition() }
        val startView = movieRecyclerView.getChildAt(0)
        scrollOffset = startView.top - movieRecyclerView.paddingTop
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_SCROLL_INDEX, scrollIndex)
        outState.putInt(EXTRA_SCROLL_OFFSET, scrollOffset)
        outState.putString(EXTRA_SORT_ORDER, movieSortOrder)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        scrollIndex = savedInstanceState.getInt(EXTRA_SCROLL_INDEX)
        scrollOffset = savedInstanceState.getInt(EXTRA_SCROLL_OFFSET)
        movieSortOrder = savedInstanceState.getString(EXTRA_SORT_ORDER, DEFAULT_SORT_ORDER)
    }

    override fun onResume() {
        super.onResume()
        fetchMovies(movieSortOrder)
    }

    private fun fetchMovies(urlEndpoint: String) {
        when (urlEndpoint) {
            NetworkUtils.endpointPopularMovies -> fetchMoviesOnlyIfDeviceOnline(urlEndpoint)
            NetworkUtils.endpointTopRatedMovies -> fetchMoviesOnlyIfDeviceOnline(urlEndpoint)
            MovieContract.ENDPOINT_FAVORITE_MOVIES -> fetchFavoriteMovies()
        }
    }

    private fun fetchMoviesOnlyIfDeviceOnline(urlEndpoint: String) {
        if (NetworkUtils.isDeviceOnline(this)) {
            val queryBundle = Bundle()
            queryBundle.putString(QUERY_TMDB_MOVIE_BUNDLE_EXTRA, urlEndpoint)
            loaderManager.restartLoader(TMDB_MOVIE_LOADER_ID, queryBundle, TmdbMoviesCallback())

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

                override fun loadInBackground(): ArrayList<Movie> {
                    val endpoint = args.getString(QUERY_TMDB_MOVIE_BUNDLE_EXTRA)
                    val movieRequestUrl = NetworkUtils.buildTmdbUrlWithSingleEndpoint(endpoint)

                    movieRequestUrl?.let {
                        try {
                            val jsonMovieString: String? = NetworkUtils.getResponseFromHttpUrl(it)
                            return TheMovieDbJsonUtils.getMovieListFromJsonString(jsonMovieString)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    return arrayListOf<Movie>()
                }
            }
        }

        override fun onLoadFinished(loader: android.support.v4.content.Loader<ArrayList<Movie>>, movieList: ArrayList<Movie>) {
            displayMoviesInGridLayout(movieList)
        }

        override fun onLoaderReset(loader: Loader<ArrayList<Movie>>) {}
    }

    private fun displayMoviesInGridLayout(moviesList: ArrayList<Movie>) {
        movieAdapter?.setMovieList(moviesList)
        scrollToSavedPositionWithOffset()

        if(moviesList.isEmpty()) Toast.makeText(this, getString(R.string.msg_movie_service_error), Toast.LENGTH_LONG).show()
    }

    private fun scrollToSavedPositionWithOffset() {
        if (scrollIndex != -1) {
            gridLayoutManager?.scrollToPositionWithOffset(scrollIndex, scrollOffset)
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
            fetchMoviesOnlyIfDeviceOnline(NetworkUtils.endpointPopularMovies)
            movieSortOrder = NetworkUtils.endpointPopularMovies
            return true
        } else if (id == R.id.action_sort_top_rated) {
            fetchMoviesOnlyIfDeviceOnline(NetworkUtils.endpointTopRatedMovies)
            movieSortOrder = NetworkUtils.endpointTopRatedMovies
            return true
        } else if (id == R.id.action_favorites) {
            movieSortOrder = MovieContract.ENDPOINT_FAVORITE_MOVIES
            fetchFavoriteMovies()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun fetchFavoriteMovies() {
        val queryBundle = Bundle()
        queryBundle.putString(QUERY_FAVORITE_MOVIES_BUNDLE_EXTRA, null)

        loaderManager.restartLoader(FAVORITE_MOVIES_LOADER_ID, queryBundle, FavoriteMoviesCallback())
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

        override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
            data?.let{
                displayMoviesInGridLayout(extractFavoriteMoviesFromCursor(it))
            }
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
        loaderManager.destroyLoader(TMDB_MOVIE_LOADER_ID)
        loaderManager.destroyLoader(FAVORITE_MOVIES_LOADER_ID)
    }

    companion object {

        val PARCELABLE_MOVIE = "parcelable_movie"

        private val GRID_LAYOUT_SPAN_PORTRAIT = 2
        private val GRID_LAYOUT_SPAN_LANDSCAPE = 3

        private val EXTRA_SCROLL_INDEX = "scroll_index"
        private val EXTRA_SCROLL_OFFSET = "scroll_offset"
        private val EXTRA_SORT_ORDER = "sort_order"
        private val DEFAULT_SORT_ORDER = NetworkUtils.endpointPopularMovies

        private val QUERY_TMDB_MOVIE_BUNDLE_EXTRA = "tmdb_movie_query"
        private val TMDB_MOVIE_LOADER_ID = 455
        private val QUERY_FAVORITE_MOVIES_BUNDLE_EXTRA = "favorite_movies"
        private val FAVORITE_MOVIES_LOADER_ID = 456
    }

}
