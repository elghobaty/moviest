package com.elghobaty.moviest.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.elghobaty.moviest";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_TRAILERS = "trailers";
    public static final String PATH_REVIEWS = "reviews";


    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final String CONTENT_TYPE_RESULTS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_TYPE_ROW = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_FAVOURED_AT = "favoured_at";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String[] getColumnList() {
            return new String[]{
                    MovieEntry._ID,
                    MovieEntry.COLUMN_TITLE,
                    MovieEntry.COLUMN_POSTER_PATH,
                    MovieEntry.COLUMN_OVERVIEW,
                    MovieEntry.COLUMN_RELEASE_DATE,
                    MovieEntry.COLUMN_RATING,
                    MovieEntry.COLUMN_FAVOURED_AT
            };
        }

        public static long getIdFromURI(Uri uri) {
            String IdString = uri.getPathSegments().get(1);
            return (null != IdString && IdString.length() > 0) ? Long.parseLong(IdString) : 0;
        }
    }

    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILERS).build();

        public static final String CONTENT_TYPE_RESULTS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;
        public static final String CONTENT_TYPE_ROW = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILERS;

        public static final String TABLE_NAME = "youtube_trailers";

        public static final String COLUMN_SOURCE = "source";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailersForMovie(long movieId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_MOVIE_ID, Long.toString(movieId)).build();
        }

        public static long getMovieIdFromUri(Uri uri) {
            String movieIdString = uri.getQueryParameter(COLUMN_MOVIE_ID);
            return (null != movieIdString && movieIdString.length() > 0) ? Long.parseLong(movieIdString) : 0;
        }

        public static String[] getColumnList() {
            return new String[]{
                    TrailerEntry._ID,
                    TrailerEntry.COLUMN_TITLE,
                    TrailerEntry.COLUMN_SOURCE,
                    TrailerEntry.COLUMN_MOVIE_ID,
            };
        }
    }


    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE_RESULTS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_TYPE_ROW = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        public static final String TABLE_NAME = "movie_reviews";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_BODY = "body";
        public static final String COLUMN_AUTHOR = "author";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReviewForMovie(long movieId) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_MOVIE_ID, Long.toString(movieId)).build();
        }


        public static long getMovieIdFromUri(Uri uri) {
            String movieIdString = uri.getQueryParameter(COLUMN_MOVIE_ID);
            return (null != movieIdString && movieIdString.length() > 0) ? Long.parseLong(movieIdString) : 0;
        }

        public static String[] getColumnList() {
            return new String[]{
                    ReviewEntry._ID,
                    ReviewEntry.COLUMN_BODY,
                    ReviewEntry.COLUMN_AUTHOR,
                    ReviewEntry.COLUMN_MOVIE_ID,
            };
        }
    }
}
