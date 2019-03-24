package com.example.zappalex.popularmovies.data

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class MovieContentProvider : ContentProvider() {
    private var movieDbHelper: MovieDbHelper? = null
    private var contentResolver: ContentResolver? = null

    override fun onCreate(): Boolean {
        val context = context
        movieDbHelper = MovieDbHelper(context)
        contentResolver = context.contentResolver
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = movieDbHelper?.readableDatabase
        val match = sUriMatcher.match(uri)
        val returnCursor: Cursor?

        when (match) {
            FAVORITES -> returnCursor = db?.query(
                    MovieContract.FavoriteMovieEntry.TABLE_NAME, null, null, null, null, null, null
            )
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        contentResolver?.let {
            returnCursor?.setNotificationUri(it, uri)
        }

        return returnCursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {

        val db = movieDbHelper?.writableDatabase
        val match = sUriMatcher.match(uri)
        val returnUri: Uri?

        when (match) {
            FAVORITES -> {
                val id = db?.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values) ?: 0
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.FavoriteMovieEntry.CONTENT_URI, id)
                } else {
                    throw android.database.SQLException("Failed to insert row into $uri")
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        contentResolver?.let {
            it.notifyChange(uri, null)
        }

        return returnUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = movieDbHelper?.writableDatabase
        val match = sUriMatcher.match(uri)
        val favoritesDeleted: Int?

        when (match) {
            FAVORITES_WITH_ID -> {
                val id = uri.pathSegments[1]
                favoritesDeleted = db?.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME, MovieContract.FavoriteMovieEntry.COLUMN_ID + "=?", arrayOf(id))
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }

        contentResolver?.let {
            it.notifyChange(uri, null)
        }

        return favoritesDeleted ?: 0
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        private val FAVORITES = 100
        private val FAVORITES_WITH_ID = 101
        private val sUriMatcher = buildUriMatcher()

        private fun buildUriMatcher(): UriMatcher {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.ENDPOINT_FAVORITE_MOVIES, FAVORITES)
            uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.ENDPOINT_FAVORITE_MOVIES + "/#", FAVORITES_WITH_ID)
            return uriMatcher
        }
    }
}
