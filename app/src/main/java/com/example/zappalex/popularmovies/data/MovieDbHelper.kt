package com.example.zappalex.popularmovies.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MovieDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.FavoriteMovieEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteMovieEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieContract.FavoriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                MovieContract.FavoriteMovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL " +
                "); "

        db.execSQL(SQL_CREATE_MOVIE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // not used at this point
    }

    companion object {
        private val DATABASE_NAME = "movie.db"
        private val DATABASE_VERSION = 1
    }

}
