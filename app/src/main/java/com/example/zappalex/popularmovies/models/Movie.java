package com.example.zappalex.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 6/14/17.
 * This class represents our movie object that will be returned by the API.
 */

public class Movie implements Parcelable {

    private String mId;
    private String mTitle;
    private String mPosterPath;
    private String mOverview;
    private String mUserRating;
    private String mReleaseDate;

    public Movie(){

    }

    private Movie(Parcel in){
        mId = in.readString();
        mTitle = in.readString();
        mPosterPath = in.readString();
        mOverview = in.readString();
        mUserRating = in.readString();
        mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeString(mUserRating);
        dest.writeString(mReleaseDate);
    }

    public static final Parcelable.Creator<Movie> CREATOR= new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public void setUserRating(String mUserRating) {
        this.mUserRating = mUserRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }
}
