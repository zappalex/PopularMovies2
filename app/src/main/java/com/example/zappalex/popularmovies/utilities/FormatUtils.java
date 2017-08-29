package com.example.zappalex.popularmovies.utilities;

import android.content.Context;

/**
 * Created by user on 6/20/17.
 * This is a util class for formatting operations.
 */

public class FormatUtils {

    public static int getDeviceOrientation(Context context){
        return context.getResources().getConfiguration().orientation;
    }

    public static String getFormattedRating(String rating){

        return rating + " / " + "10";
    }

    // Date will be given in "yyyy-MM-dd" format, we just need to take the first 4 characters.
    public static String getYearFromDateString(String dateString){
        String dateToReturn;
        dateToReturn = dateString.substring(0,4);
        return dateToReturn;
    }

}
