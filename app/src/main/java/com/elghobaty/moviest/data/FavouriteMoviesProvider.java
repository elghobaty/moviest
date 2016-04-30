package com.elghobaty.moviest.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class FavouriteMoviesProvider extends ContentProvider {

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private MoviesDBOpenHelper mDBHelper;

    private static final int BASE_MOVIE_CODE = 100;
    private static final int SINGLE_MOVIE_CODE = 101;
    private static final int BASE_TRAILER_CODE = 200;
    private static final int BASE_REVIEW_CODE = 300;

    private static final String mWhereIdClause = MoviesContract.MovieEntry._ID + " = ?";

    static {
        sURIMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES, BASE_MOVIE_CODE);
        sURIMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_MOVIES + "/#", SINGLE_MOVIE_CODE);
        sURIMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_TRAILERS, BASE_TRAILER_CODE);
        sURIMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, MoviesContract.PATH_REVIEWS, BASE_REVIEW_CODE);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new MoviesDBOpenHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        switch (sURIMatcher.match(uri)) {
            case SINGLE_MOVIE_CODE:
                return MoviesContract.MovieEntry.CONTENT_TYPE_ROW;

            case BASE_MOVIE_CODE:
                return MoviesContract.MovieEntry.CONTENT_TYPE_RESULTS;

            case BASE_REVIEW_CODE:
                return MoviesContract.ReviewEntry.CONTENT_TYPE_RESULTS;

            case BASE_TRAILER_CODE:
                return MoviesContract.TrailerEntry.CONTENT_TYPE_RESULTS;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase DB = mDBHelper.getWritableDatabase();

        if (sURIMatcher.match(uri) != BASE_MOVIE_CODE) {
            throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        long _ID = DB.insertOrThrow(MoviesContract.MovieEntry.TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri, null);

        return MoviesContract.MovieEntry.buildMovieUri(_ID);
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase DB = mDBHelper.getWritableDatabase();

        String tableName = getCorrespondingTableName(uri, new int[]{BASE_TRAILER_CODE, BASE_REVIEW_CODE});

        int returnCount = 0;
        try {
            DB.beginTransaction();
            for (ContentValues value : values) {
                long _id = DB.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            DB.setTransactionSuccessful();
        } finally {
            DB.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnCount;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor ret;

        SQLiteDatabase DB = mDBHelper.getReadableDatabase();

        String tableName = getCorrespondingTableName(uri, new int[]{SINGLE_MOVIE_CODE, BASE_MOVIE_CODE, BASE_TRAILER_CODE, BASE_REVIEW_CODE});

        switch (sURIMatcher.match(uri)) {
            case SINGLE_MOVIE_CODE:
                ret = DB.query(tableName, projection, mWhereIdClause, movieIdToSelectionArgs(uri), null, null, sortOrder, "1");
                break;

            case BASE_MOVIE_CODE:
                ret = DB.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                if (selection == null && selectionArgs == null) {
                    selection = whereMovieId(uri);
                    selectionArgs = new String[]{movieIdFromURL(uri)};
                }
                ret = DB.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
        }

        ret.setNotificationUri(getContext().getContentResolver(), uri);

        return ret;
    }

    private String whereMovieId(Uri uri) {

        switch (sURIMatcher.match(uri)) {
            case BASE_TRAILER_CODE:
                return MoviesContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?";

            case BASE_REVIEW_CODE:
                return MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?";
        }

        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }

    private String movieIdFromURL(Uri uri) {

        switch (sURIMatcher.match(uri)) {
            case BASE_TRAILER_CODE:
                return String.valueOf(MoviesContract.TrailerEntry.getMovieIdFromUri(uri));

            case BASE_REVIEW_CODE:
                return String.valueOf(MoviesContract.ReviewEntry.getMovieIdFromUri(uri));
        }

        throw new UnsupportedOperationException("Unknown URI: " + uri);
    }

    private String[] movieIdToSelectionArgs(Uri uri) {
        return new String[]{ String.valueOf(MoviesContract.MovieEntry.getIdFromURI(uri)) };
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase DB = mDBHelper.getWritableDatabase();

        if (sURIMatcher.match(uri) != SINGLE_MOVIE_CODE) {
            throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        int affectedRows = DB.update(MoviesContract.MovieEntry.TABLE_NAME, values, mWhereIdClause, movieIdToSelectionArgs(uri));

        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase DB = mDBHelper.getWritableDatabase();

        selection = selection == null ? "1" : selection;

        String tableName = getCorrespondingTableName(uri, new int[]{SINGLE_MOVIE_CODE, BASE_TRAILER_CODE, BASE_REVIEW_CODE});

        int matchCode = sURIMatcher.match(uri);

        DB.beginTransaction();
        int affectedRows = 0;
        try {
            if (matchCode == SINGLE_MOVIE_CODE) {
                long movieId = MoviesContract.MovieEntry.getIdFromURI(uri);
                delete(MoviesContract.TrailerEntry.buildTrailersForMovie(movieId), null, null);
                delete(MoviesContract.ReviewEntry.buildReviewForMovie(movieId), null, null);
                selection = mWhereIdClause;
                selectionArgs = movieIdToSelectionArgs(uri);
            }
            affectedRows = DB.delete(tableName, selection, selectionArgs);
            DB.setTransactionSuccessful();
        } finally {
            DB.endTransaction();
        }

        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }


    private String getCorrespondingTableName(Uri uri, int[] allowedURICodes) {
        int matchCode = sURIMatcher.match(uri);

        boolean isAllowed = false;

        for (int allowedCode : allowedURICodes) {
            if (allowedCode == matchCode) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        switch (matchCode) {
            case SINGLE_MOVIE_CODE:
            case BASE_MOVIE_CODE:
                return MoviesContract.MovieEntry.TABLE_NAME;

            case BASE_TRAILER_CODE:
                return MoviesContract.TrailerEntry.TABLE_NAME;

            case BASE_REVIEW_CODE:
                return MoviesContract.ReviewEntry.TABLE_NAME;
        }

        return null;
    }

}
