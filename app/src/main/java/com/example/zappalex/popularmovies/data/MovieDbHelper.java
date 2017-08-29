package com.example.zappalex.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteMovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                "); ";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }

}
