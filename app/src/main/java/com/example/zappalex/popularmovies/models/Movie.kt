package com.example.zappalex.popularmovies.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Movie (
        val id: String,
        val title: String,
        val posterPath: String,
        val overview: String,
        val userRating: String,
        val releaseDate: String

) : Parcelable
