package com.example.zappalex.popularmovies.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.zappalex.popularmovies.R
import com.example.zappalex.popularmovies.models.Review

import java.util.ArrayList

class ReviewAdapter : RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>() {
    private var reviewList: ArrayList<Review>? = null

    inner class ReviewAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewAuthorTextView: TextView
        val reviewContentTextView: TextView

        init {
            reviewAuthorTextView = view.findViewById(R.id.reviewAuthorTextView) as TextView
            reviewContentTextView = view.findViewById(R.id.reviewContentTextView) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewAdapterViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.review_list_item

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)

        return ReviewAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewAdapterViewHolder, position: Int) {
        reviewList?.let {
            val (_, author, content) = it[position]
            holder.reviewAuthorTextView.text = author
            holder.reviewContentTextView.text = content
        }
    }

    override fun getItemCount(): Int {
        return reviewList?.size ?: 0
    }

    fun setReviewList(reviewList: ArrayList<Review>) {
        this.reviewList = reviewList
        notifyDataSetChanged()
    }
}
