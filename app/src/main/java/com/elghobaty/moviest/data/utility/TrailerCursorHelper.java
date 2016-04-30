package com.elghobaty.moviest.data.utility;

import android.database.Cursor;
import com.elghobaty.moviest.data.MoviesContract;
import com.elghobaty.moviest.data.model.Trailer;

class TrailerCursorHelper {

    public static String getTitle(Cursor cursor) {
        int index = cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_TITLE);
        return cursor.getString(index);
    }


    public static String getSource(Cursor cursor) {
        int index = cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_SOURCE);
        return cursor.getString(index);
    }

    public static long getID(Cursor cursor) {
        int index = cursor.getColumnIndex(MoviesContract.TrailerEntry._ID);
        return cursor.getLong(index);
    }

    public static long getMovieId(Cursor cursor) {
        int index = cursor.getColumnIndex(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID);
        return cursor.getLong(index);
    }

    public static Trailer[] getTrailers(Cursor cursor) {
        if (cursor == null)
            return null;

        Trailer[] trailers = null;

        if (cursor.getCount() > 0) {
            trailers = new Trailer[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                trailers[i++] = new Trailer(getID(cursor), getTitle(cursor), getSource(cursor));
            }
        }

        return trailers;
    }
}
