package com.elghobaty.moviest.data.utility;

import android.database.Cursor;
import com.elghobaty.moviest.data.MoviesContract;
import com.elghobaty.moviest.data.model.Review;

class ReviewCursorHelper {

    private static String getAuthor(Cursor cursor){
        int index = cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR);
        return cursor.getString(index);
    }

    public static String getBody(Cursor cursor){
        int index = cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_BODY);
        return cursor.getString(index);
    }

    public static long getID(Cursor cursor){
        int index = cursor.getColumnIndex(MoviesContract.ReviewEntry._ID);
        return cursor.getLong(index);
    }

    public static long getMovieId(Cursor cursor){
        int index = cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID);
        return cursor.getLong(index);
    }


    public static Review[] getReviews(Cursor cursor) {
        if (cursor == null)
            return null;

        Review[] reviews = null;

        if (cursor.getCount() > 0) {
            reviews = new Review[cursor.getCount()];
            int i = 0;
            while (cursor.moveToNext()) {
                reviews[i++] = new Review(getID(cursor),getBody(cursor), getAuthor(cursor));
            }
        }

        return reviews;
    }


}
