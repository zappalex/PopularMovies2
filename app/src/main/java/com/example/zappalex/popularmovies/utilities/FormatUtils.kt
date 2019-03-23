package com.example.zappalex.popularmovies.utilities

import android.content.Context

import java.text.Format

/**
 * Created by user on 6/20/17.
 * This is a util class for formatting operations.
 */

object FormatUtils {

    fun getDeviceOrientation(context: Context): Int {
        return context.resources.configuration.orientation
    }

    fun getFormattedRating(rating: String): String {
        return "$rating / 10"
    }

    // Date will be given in "yyyy-MM-dd" format, we just need to take the first 4 characters.
    fun getYearFromDateString(dateString: String): String {
        val dateToReturn: String
        dateToReturn = dateString.substring(0, 4)
        return dateToReturn
    }

}
