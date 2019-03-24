package com.example.zappalex.popularmovies.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.zappalex.popularmovies.R
import com.example.zappalex.popularmovies.models.Movie
import com.example.zappalex.popularmovies.utilities.NetworkUtils
import com.squareup.picasso.Picasso
import java.util.ArrayList

/**
 * Created by user on 6/15/17.
 * This is an adapter class for the GridLayout objects
 */

class MovieAdapter(private val movieClickHandler: MovieAdapterOnClickHandler) : RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>() {

    private var movieList: ArrayList<Movie>? = null

    interface MovieAdapterOnClickHandler {
        fun onClick(movie: Movie)
    }

    inner class MovieAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val movieImageView: ImageView

        init {
            movieImageView = view.findViewById(R.id.moviePosterImg) as ImageView
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            movieList?.let {
                val clickedMovie = it[adapterPosition]
                movieClickHandler.onClick(clickedMovie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapterViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.movie_list_item

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)

        return MovieAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapterViewHolder, position: Int) {
        movieList?.let {
            val currentMovie = it[position]
            val currentPosterPath = currentMovie.posterPath

            // Size "w185" was recommended here, but when checked on phone, it looked blurry in a 2 column layout (portrait) or 3 column layout (land).
            val picassoImgUrl = NetworkUtils.buildPicassoUrl(currentPosterPath)
            Picasso.with(holder.movieImageView.context).load(picassoImgUrl).into(holder.movieImageView)
        }
    }

    override fun getItemCount(): Int {
        return movieList?.size ?: 0
    }

    fun setMovieList(movieList: ArrayList<Movie>) {
        this.movieList = movieList
        notifyDataSetChanged()
    }
}
