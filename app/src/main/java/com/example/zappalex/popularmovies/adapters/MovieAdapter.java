package com.example.zappalex.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.zappalex.popularmovies.R;
import com.example.zappalex.popularmovies.models.Movie;
import com.example.zappalex.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by user on 6/15/17.
 * This is an adapter class for the GridLayout objects
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovieList;
    private final MovieAdapterOnClickHandler mMovieClickHandler;


    public interface MovieAdapterOnClickHandler{
        void onClick(Movie movie);
    }

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mMovieClickHandler = clickHandler;
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view){
            super(view);
            mMovieImageView = (ImageView)view.findViewById(R.id.img_movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie clickedMovie = mMovieList.get(adapterPosition);
            mMovieClickHandler.onClick(clickedMovie);
        }
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie currentMovie = mMovieList.get(position);
        String currentPosterPath = currentMovie.getPosterPath();

        // Size "w185" was recommended here, but when checked on phone, it looked blurry in a 2 column layout (portrait) or 3 column layout (land).
        String picassoImgUrl = NetworkUtils.buildPicassoUrl(currentPosterPath);
        Picasso.with(holder.mMovieImageView.getContext()).load(picassoImgUrl).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if(mMovieList == null){
            return 0;
        }else{
            return mMovieList.size();
        }
    }

    public void setMovieList(ArrayList<Movie> movieList){
        mMovieList = movieList;
        notifyDataSetChanged();
    }
}
