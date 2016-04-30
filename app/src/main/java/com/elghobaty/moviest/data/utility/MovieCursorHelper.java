package com.elghobaty.moviest.data.utility;

import android.database.Cursor;
import com.elghobaty.moviest.data.MoviesContract;

public class MovieCursorHelper {


    public static long getID(Cursor cursor) {
        return cursor.getLong(cursor.getColumnIndex(MoviesContract.MovieEntry._ID));
    }

    public static String getTitle(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
    }

    public static String getOverview(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_OVERVIEW));
    }

    public static double getRating(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING));
    }

    public static String getPosterPath(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_POSTER_PATH));
    }

    public static String getReleaseDate(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE));
    }
}
