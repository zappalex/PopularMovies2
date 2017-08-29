package com.example.zappalex.popularmovies;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.zappalex.popularmovies.adapters.MovieAdapter;
import com.example.zappalex.popularmovies.data.MovieContract;
import com.example.zappalex.popularmovies.models.Movie;
import com.example.zappalex.popularmovies.utilities.FormatUtils;
import com.example.zappalex.popularmovies.utilities.NetworkUtils;
import com.example.zappalex.popularmovies.utilities.TheMovieDbJsonUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    public static final String PARCELABLE_MOVIE = "parcelable_movie";

    private static final int GRID_LAYOUT_SPAN_PORTRAIT = 2;
    private static final int GRID_LAYOUT_SPAN_LANDSCAPE = 3;

    private static final String QUERY_TMDB_MOVIE_BUNDLE_EXTRA = "tmdb_movie_query";
    private static final int TMDB_MOVIE_LOADER_ID = 455;
    private static final String QUERY_FAVORITE_MOVIES_BUNDLE_EXTRA = "favorite_movies";
    private static final int FAVORITE_MOVIES_LOADER_ID = 456;

    private MovieAdapter mMovieAdapter;
    private LoaderManager mLoaderManager;
    private GridLayoutManager mGridLayoutManager;

    // This is used to determine which loader to keep alive when navigating to movieDetailActivity.
    private boolean mCurrentlyShowingFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoaderManager = getSupportLoaderManager();

        RecyclerView movieRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_list);
        mGridLayoutManager = initializeGridLayoutManager();
        movieRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieAdapter = new MovieAdapter(this);
        movieRecyclerView.setAdapter(mMovieAdapter);

        fetchMoviesOnlyIfDeviceOnline(NetworkUtils.ENDPOINT_POPULAR_MOVIES);
    }

    // in portrait, grid will have 2 columns and in landscape grid will have 3.
    private GridLayoutManager initializeGridLayoutManager() {
        GridLayoutManager gridLayoutManager;
        int deviceOrientation = FormatUtils.getDeviceOrientation(this);

        if (deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_PORTRAIT, GridLayoutManager.VERTICAL, false);
        } else {
            gridLayoutManager = new GridLayoutManager(this, GRID_LAYOUT_SPAN_LANDSCAPE, GridLayoutManager.VERTICAL, false);
        }
        return gridLayoutManager;
    }

    private void fetchMoviesOnlyIfDeviceOnline(String urlEndpoint) {
        if (NetworkUtils.isDeviceOnline(this)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(QUERY_TMDB_MOVIE_BUNDLE_EXTRA, urlEndpoint);

            mLoaderManager.restartLoader(TMDB_MOVIE_LOADER_ID, queryBundle, new TmdbMoviesCallback());

        } else {
            Toast.makeText(this, getString(R.string.msg_movies_offline), Toast.LENGTH_SHORT).show();
        }
    }

    private class TmdbMoviesCallback implements LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

        @Override
        public android.support.v4.content.Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<Movie>>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public ArrayList<Movie> loadInBackground() {
                    String endpoint = args.getString(QUERY_TMDB_MOVIE_BUNDLE_EXTRA);
                    URL movieRequestUrl = NetworkUtils.buildTmdbUrlWithSingleEndpoint(endpoint);

                    if (movieRequestUrl != null) {
                        try {
                            String jsonMovieString = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                            return TheMovieDbJsonUtils.getMovieListFromJsonString(jsonMovieString);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            };
        }

        @Override
        public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Movie>> loader, ArrayList<Movie> movieList) {
            displayMoviesInGridLayout(movieList);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        }
    }

    private void displayMoviesInGridLayout(ArrayList<Movie> moviesList) {
        if (moviesList != null) {
            mMovieAdapter.setMovieList(moviesList);
            mGridLayoutManager.scrollToPositionWithOffset(0, 0);
        } else {
            Toast.makeText(this, getString(R.string.msg_movie_service_error), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mCurrentlyShowingFavorites = false;

        if (id == R.id.action_sort_popular) {
            fetchMoviesOnlyIfDeviceOnline(NetworkUtils.ENDPOINT_POPULAR_MOVIES);
            return true;
        } else if (id == R.id.action_sort_top_rated) {
            fetchMoviesOnlyIfDeviceOnline(NetworkUtils.ENDPOINT_TOP_RATED_MOVIES);
            return true;
        } else if (id == R.id.action_favorites) {
            mCurrentlyShowingFavorites = true;
            fetchFavoriteMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchFavoriteMovies() {
        Bundle queryBundle = new Bundle();
        queryBundle.putString(QUERY_FAVORITE_MOVIES_BUNDLE_EXTRA, null);

        mLoaderManager.restartLoader(FAVORITE_MOVIES_LOADER_ID, queryBundle, new FavoriteMoviesCallback());
    }

    private class FavoriteMoviesCallback implements LoaderManager.LoaderCallbacks<Cursor>{

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<Cursor>(getBaseContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            displayMoviesInGridLayout(extractFavoriteMoviesFromCursor(data));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }

    private ArrayList<Movie> extractFavoriteMoviesFromCursor(Cursor favoritesCursor) {
        ArrayList<Movie> movieList = new ArrayList<>();
        try {
            while (favoritesCursor.moveToNext()) {
                Movie currentMovie = new Movie();
                currentMovie.setId(favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_ID)));
                currentMovie.setTitle(favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_TITLE)));
                currentMovie.setPosterPath(favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH)));
                currentMovie.setOverview(favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW)));
                currentMovie.setUserRating(favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_USER_RATING)));
                currentMovie.setReleaseDate(favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE)));
                movieList.add(currentMovie);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            favoritesCursor.close();
        }
        return movieList;
    }

    @Override
    public void onClick(Movie movie) {
        destroyLoaders();

        Context context = this;
        Class destinationActivity = MovieDetailActivity.class;
        Intent intentStartMovieDetail = new Intent(context, destinationActivity);
        intentStartMovieDetail.putExtra(PARCELABLE_MOVIE, movie);
        startActivity(intentStartMovieDetail);
    }

    // we are destroying loaders because they will both automatically be called upon return to main activity
    private void destroyLoaders(){
        if(mLoaderManager != null){
            mLoaderManager.destroyLoader(TMDB_MOVIE_LOADER_ID);
            if(mCurrentlyShowingFavorites == false){
                mLoaderManager.destroyLoader(FAVORITE_MOVIES_LOADER_ID);
            }
        }
    }

}
