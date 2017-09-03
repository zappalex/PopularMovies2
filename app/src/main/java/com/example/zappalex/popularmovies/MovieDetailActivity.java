package com.example.zappalex.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zappalex.popularmovies.adapters.ReviewAdapter;
import com.example.zappalex.popularmovies.adapters.VideoAdapter;
import com.example.zappalex.popularmovies.data.MovieContract;
import com.example.zappalex.popularmovies.models.Movie;
import com.example.zappalex.popularmovies.models.Review;
import com.example.zappalex.popularmovies.models.Video;
import com.example.zappalex.popularmovies.utilities.FormatUtils;
import com.example.zappalex.popularmovies.utilities.NetworkUtils;
import com.example.zappalex.popularmovies.utilities.TheMovieDbJsonUtils;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetailActivity extends AppCompatActivity implements VideoAdapter.VideoAdapterOnClickHandler {

    private static final String EXTRA_SCROLL_POSITION = "scroll_position";
    private static final String VIDEOS_QUERY_BUNDLE_EXTRA = "videos_query";
    private static final String REVIEWS_QUERY_BUNDLE_EXTRA = "reviews_query";
    private static final int VIDEOS_LOADER_ID = 56;
    private static final int REVIEWS_LOADER_ID = 57;

    private NestedScrollView mMovieDetailNestedScrollView;
    private TextView mMovieTitleTextView;
    private ImageView mMoviePosterImg;
    private TextView mMovieDateTextView;
    private TextView mMovieRatingTextView;
    private ImageView mMovieFavoriteImageView;
    private TextView mMovieOverviewTextView;

    private Movie mCurrentMovie;
    private boolean mIsMovieInFavorites = false;

    private ArrayList<Video> mVideoList;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        initUiComponents();
        initLayoutAdapters();
        retrieveIntents();

        mIsMovieInFavorites = isMovieInFavorites(queryCurrentMovie(), mCurrentMovie);
        toggleFavoritesHeartImage(mIsMovieInFavorites);
    }

    private void initUiComponents() {
        mMovieDetailNestedScrollView = (NestedScrollView) findViewById(R.id.movie_detail_nested_scroll);
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_title_movie);
        mMoviePosterImg = (ImageView) findViewById(R.id.img_movie_poster);
        mMovieDateTextView = (TextView) findViewById(R.id.tv_movie_date);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_rating);
        mMovieFavoriteImageView = (ImageView) findViewById(R.id.iv_favorite);
        mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);

    }

    private void initLayoutAdapters() {
        RecyclerView videoRecyclerView = (RecyclerView) findViewById(R.id.rv_videos);
        videoRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager videosLinearLayoutManager = new LinearLayoutManager(this);
        videoRecyclerView.setLayoutManager(videosLinearLayoutManager);

        mVideoAdapter = new VideoAdapter(this);
        videoRecyclerView.setAdapter(mVideoAdapter);

        RecyclerView reviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        reviewRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager reviewsLinearLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setLayoutManager(reviewsLinearLayoutManager);

        mReviewAdapter = new ReviewAdapter();
        reviewRecyclerView.setAdapter(mReviewAdapter);
    }

    private void retrieveIntents() {
        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity != null && intentThatStartedActivity.hasExtra(MainActivity.PARCELABLE_MOVIE)) {
            mCurrentMovie = intentThatStartedActivity.getParcelableExtra(MainActivity.PARCELABLE_MOVIE);
            populateViews(mCurrentMovie);

            String videosEndpoint = NetworkUtils.formatVideosEndpointWithId(mCurrentMovie.getId());
            fetchVideosOnlyIfOnline(videosEndpoint);

            String reviewsEndpoint = NetworkUtils.formatReviewsEndpointWithId(mCurrentMovie.getId());
            fetchReviewsOnlyIfOnline(reviewsEndpoint);

        } else {
            Toast.makeText(this, getString(R.string.msg_movie_detail_error), Toast.LENGTH_LONG).show();
        }
    }

    private void populateViews(Movie movie) {
        mMovieTitleTextView.setText(movie.getTitle());
        mMovieDateTextView.setText(FormatUtils.getYearFromDateString(movie.getReleaseDate()));
        mMovieRatingTextView.setText(FormatUtils.getFormattedRating(movie.getUserRating()));
        mMovieOverviewTextView.setText(movie.getOverview());

        String picassoImgUrl = NetworkUtils.buildPicassoUrl(movie.getPosterPath());
        Picasso.with(this).load(picassoImgUrl).into(mMoviePosterImg);
    }

    private void fetchVideosOnlyIfOnline(String videosEndpoint) {
        if (NetworkUtils.isDeviceOnline(this)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(VIDEOS_QUERY_BUNDLE_EXTRA, videosEndpoint);

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(VIDEOS_LOADER_ID, queryBundle, new VideosCallback());

        } else {
            Toast.makeText(this, getString(R.string.msg_videos_offline), Toast.LENGTH_LONG).show();
        }
    }

    private void fetchReviewsOnlyIfOnline(String reviewsEndpoint) {
        if (NetworkUtils.isDeviceOnline(this)) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString(REVIEWS_QUERY_BUNDLE_EXTRA, reviewsEndpoint);

            LoaderManager loaderManager = getSupportLoaderManager();
            loaderManager.initLoader(REVIEWS_LOADER_ID, queryBundle, new ReviewsCallback());
        } else {
            Toast.makeText(this, getString(R.string.msg_reviews_offline), Toast.LENGTH_LONG).show();
        }
    }

    private class VideosCallback implements LoaderManager.LoaderCallbacks<ArrayList<Video>> {
        @Override
        public Loader<ArrayList<Video>> onCreateLoader(int id, final Bundle args) {
            String videosEndpoint = args.getString(VIDEOS_QUERY_BUNDLE_EXTRA);
            return new VideoAsyncTaskLoader(MovieDetailActivity.this, videosEndpoint);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Video>> loader, ArrayList<Video> data) {
            mVideoList = data;
            if (mVideoAdapter != null) {
                mVideoAdapter.setVideoList(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Video>> loader) {
        }
    }

    private static class VideoAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Video>> {

        private ArrayList<Video> mCachedVideos;
        private String mVideosEndpoint = "";

        public VideoAsyncTaskLoader (Context context, String videosEndpoint){
            super(context);
            mVideosEndpoint = videosEndpoint;
        }

        @Override
        protected void onStartLoading() {
            if (mCachedVideos != null) {
                deliverResult(mCachedVideos);
            } else {
                forceLoad();
            }
        }

        @Override
        public ArrayList<Video> loadInBackground() {
            URL videosRequestUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(mVideosEndpoint);
            if (videosRequestUrl != null) {
                try {
                    String jsonVideosString = NetworkUtils.getResponseFromHttpUrl(videosRequestUrl);
                    return TheMovieDbJsonUtils.getVideoListFromJsonString(jsonVideosString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void deliverResult(ArrayList<Video> data) {
            mCachedVideos = data;
            super.deliverResult(data);
        }
    }

    private class ReviewsCallback implements LoaderManager.LoaderCallbacks<ArrayList<Review>> {
        @Override
        public Loader<ArrayList<Review>> onCreateLoader(int id, final Bundle args) {
           String reviewsEndpoint = args.getString(REVIEWS_QUERY_BUNDLE_EXTRA);
            return new ReviewsAsyncTaskLoader(MovieDetailActivity.this, reviewsEndpoint);
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
            if (mReviewAdapter != null) {
                mReviewAdapter.setReviewList(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Review>> loader) {
        }
    }

    private static class ReviewsAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Review>>{

        private ArrayList<Review> mCachedReviews;
        private String mReviewsEndpoint = "";

        public ReviewsAsyncTaskLoader(Context context, String reviewsEndpoint){
            super(context);
            mReviewsEndpoint = reviewsEndpoint;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();

            if(mCachedReviews != null){
                deliverResult(mCachedReviews);
            }else{
                forceLoad();
            }
        }

        @Override
        public ArrayList<Review> loadInBackground() {
            URL reviewsRequestUrl = NetworkUtils.buildTmdbUrlWithPathEndpoint(mReviewsEndpoint);
            if (reviewsRequestUrl != null) {
                try {
                    String jsonReviewsString = NetworkUtils.getResponseFromHttpUrl(reviewsRequestUrl);
                    return TheMovieDbJsonUtils.getReviewListFromJsonString(jsonReviewsString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void deliverResult(ArrayList<Review> data) {
            mCachedReviews = data;
            super.deliverResult(data);
        }
    };

    @Override
    public void onCLick(int position) {
        if (mVideoList != null && mVideoList.size() > 0) {
            Uri youtubeUri = NetworkUtils.buildYouTubeUriWithVideoKey(mVideoList.get(position).getKey());
            Intent viewVideoIntent = new Intent(Intent.ACTION_VIEW, youtubeUri);

            if (viewVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(viewVideoIntent);
            }
        }
    }

    private boolean isMovieInFavorites(Cursor favoritesCursor, Movie currentMovie) {

        try {
            while (favoritesCursor.moveToNext()) {
                String cursorMovieId = favoritesCursor.getString(favoritesCursor.getColumnIndex(MovieContract.FavoriteMovieEntry.COLUMN_ID));
                if (currentMovie.getId().equals(cursorMovieId)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            favoritesCursor.close();
        }

        return false;
    }

    private Cursor queryCurrentMovie() {
        return getContentResolver().query(MovieContract.FavoriteMovieEntry.CONTENT_URI,
                null,
                null,
                new String[]{mCurrentMovie.getId()},
                null);
    }

    private void toggleFavoritesHeartImage(boolean isFavorite) {
        if (isFavorite) {
            mMovieFavoriteImageView.setImageResource(R.drawable.heart_red);
        } else {
            mMovieFavoriteImageView.setImageResource(R.drawable.heart_grey);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int scrollPosition = mMovieDetailNestedScrollView.getScrollY();
        outState.putInt(EXTRA_SCROLL_POSITION, scrollPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        int scrollPosition = savedInstanceState.getInt(EXTRA_SCROLL_POSITION);
        mMovieDetailNestedScrollView.scrollTo(0, scrollPosition);
    }

    public void onFavoriteClick(View view) {
        handleFavoriteClickLogic();
    }

    private void handleFavoriteClickLogic() {
        if (mIsMovieInFavorites) {
            deleteMoveFromFavoritesDb();
            mIsMovieInFavorites = false;
        } else {
            insertMovieIntoFavoritesDb();
            mIsMovieInFavorites = true;
        }
        toggleFavoritesHeartImage(mIsMovieInFavorites);
    }

    private void deleteMoveFromFavoritesDb() {

        String idOfMovieToDelete = mCurrentMovie.getId();
        int favoriteDeleted = 0;

        if (idOfMovieToDelete != null) {
            Uri uriToDelete = MovieContract.FavoriteMovieEntry.CONTENT_URI.buildUpon().appendPath(mCurrentMovie.getId()).build();
            favoriteDeleted = getContentResolver().delete(uriToDelete, null, null);
        }

        if (favoriteDeleted == 0) {
            Toast.makeText(this, getString(R.string.action_movie_deleted_from_favorites_failure), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.action_movie_deleted_from_favorites), Toast.LENGTH_SHORT).show();
        }
    }

    private void insertMovieIntoFavoritesDb() {
        ContentValues favoriteMovieContentValues = createFavoriteMovieContentValues();
        Uri insertResultUri = null;

        if (favoriteMovieContentValues != null) {
            insertResultUri = getContentResolver().insert(MovieContract.FavoriteMovieEntry.CONTENT_URI, favoriteMovieContentValues);
        }

        if (insertResultUri != null) {
            displayStatusOfSuccessfulInsert(true);
        } else {
            displayStatusOfSuccessfulInsert(false);
        }
    }

    private ContentValues createFavoriteMovieContentValues() {
        if (mCurrentMovie != null) {
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_ID, mCurrentMovie.getId());
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_TITLE, mCurrentMovie.getTitle());
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH, mCurrentMovie.getPosterPath());
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW, mCurrentMovie.getOverview());
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_USER_RATING, mCurrentMovie.getUserRating());
            cv.put(MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE, mCurrentMovie.getReleaseDate());
            return cv;
        } else {
            return null;
        }
    }

    private void displayStatusOfSuccessfulInsert(boolean isInsertSuccessful) {
        if (isInsertSuccessful) {
            Toast.makeText(this, getString(R.string.action_movie_added_to_favorites_success), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getString(R.string.action_movie_added_to_favorites_failure), Toast.LENGTH_LONG).show();
        }
    }

}
