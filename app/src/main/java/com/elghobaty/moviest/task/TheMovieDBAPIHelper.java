package com.elghobaty.moviest.task;

import android.net.Uri;
import com.elghobaty.moviest.BuildConfig;
import com.elghobaty.moviest.utility.MovieListSorting;

class TheMovieDBAPIHelper implements TheMovieDBAPI {

    public static Uri getListRequestURI() {

        if (MovieListSorting.isTopRated()) {
            return getTopRatedRequestURI();
        }

        if (MovieListSorting.isPopular()) {
            return getPopularRequestURI();
        }

        throw new UnsupportedOperationException(MovieListSorting.getSortingMode() + " is not supported.");
    }

    public static Uri getDetailRequestURI(long movieId) {
        return Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(String.valueOf(movieId))
                .appendQueryParameter(APPEND_TO_RESPONSE, TRAILERS + "," + REVIEWS)
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
    }

    private static Uri getPopularRequestURI() {
        return Uri.parse(BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(DISCOVER)
                .appendPath(MOVIE)
                .appendQueryParameter(SORT_BY, POPULAR)
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
    }

    private static Uri getTopRatedRequestURI() {
        return Uri.parse(BASE_URL).buildUpon()
                .appendPath(API_VERSION)
                .appendPath(MOVIE)
                .appendPath(TOP_RATED)
                .appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY)
                .build();
    }
}
