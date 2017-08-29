package com.example.zappalex.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zappalex.popularmovies.R;
import com.example.zappalex.popularmovies.models.Review;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {

    private ArrayList<Review> mReviewList;

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mReviewAuthorTextView;
        public final TextView mReviewContentTextView;

        public ReviewAdapterViewHolder(View view) {
            super(view);

            mReviewAuthorTextView = (TextView) view.findViewById(R.id.tv_review_author);
            mReviewContentTextView = (TextView) view.findViewById(R.id.tv_review_content);
        }
    }


    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        Review currentReview = mReviewList.get(position);
        holder.mReviewAuthorTextView.setText(currentReview.getAuthor());
        holder.mReviewContentTextView.setText(currentReview.getContent().trim());
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) {
            return 0;
        } else {
            return mReviewList.size();
        }
    }

    public void setReviewList(ArrayList<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }
}
