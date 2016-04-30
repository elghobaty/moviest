package com.elghobaty.moviest.data.utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.elghobaty.moviest.data.MoviesContract;
import com.elghobaty.moviest.data.model.Movie;
import com.elghobaty.moviest.data.model.Review;
import com.elghobaty.moviest.data.model.Trailer;
import com.elghobaty.moviest.task.PersistRemoteImage;
import com.elghobaty.moviest.utility.DateHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.ParseException;

public class FavouredMovie {

    private final Movie mMovie;
    private boolean mFavourite = false;

    private final ContentResolver mContentResolver;
    private final Activity mActivity;

    public FavouredMovie(Activity activity, Movie movie) {
        mContentResolver = activity.getContentResolver();
        mActivity = activity;
        mMovie = movie;
        fetchMovieFromDatabase();
    }

    private void fetchMovieFromDatabase() {
        Cursor movieRowCursor = null;
        try {
            movieRowCursor = mContentResolver.query(
                    MoviesContract.MovieEntry.buildMovieUri(mMovie.getId()),
                    null,
                    null,
                    null,
                    null
            );
            if (movieRowCursor != null && movieRowCursor.moveToNext()) {
                mFavourite = true;
                fetchTrailers();
                fetchReviews();
            }
        } finally {
            if (movieRowCursor != null) {
                movieRowCursor.close();
            }
        }
    }

    private void fetchTrailers() {
        Cursor trailersCursor = null;
        try {
            trailersCursor = mContentResolver.query(
                    MoviesContract.TrailerEntry.buildTrailersForMovie(mMovie.getId()),
                    null,
                    null,
                    null,
                    null
            );
            mMovie.setTrailers(TrailerCursorHelper.getTrailers(trailersCursor));
        } finally {
            if (trailersCursor != null) {
                trailersCursor.close();
            }
        }
    }

    private void fetchReviews() {
        Cursor reviewsCursor = null;
        try {
            reviewsCursor = mContentResolver.query(
                    MoviesContract.ReviewEntry.buildReviewForMovie(mMovie.getId()),
                    null,
                    null,
                    null,
                    null
            );
            mMovie.setReviews(ReviewCursorHelper.getReviews(reviewsCursor));
        } finally {
            if (reviewsCursor != null) {
                reviewsCursor.close();
            }
        }
    }


    public void setTrailers(Trailer[] trailers) {
        if (!mMovie.trailersSet()) {
            mMovie.setTrailers(trailers);
            if (mFavourite) {
                storeTrailers();
            }
        }
    }


    public void setReviews(Review[] reviews) {
        if (!mMovie.reviewsSet()) {
            mMovie.setReviews(reviews);
            if (mFavourite) {
                storeReviews();
            }
        }
    }

    public boolean isFavourite() {
        return mFavourite;
    }

    public void favour() {
        persistMovie();
    }

    public void UnFavour() {
        deleteMovie();
    }

    private void persistMovie() {
        final ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry._ID, mMovie.getId());
        values.put(MoviesContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
        values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
        values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
        values.put(MoviesContract.MovieEntry.COLUMN_RATING, mMovie.getRating());
        values.put(MoviesContract.MovieEntry.COLUMN_FAVOURED_AT, DateHelper.getDateTimeNowString());
        values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());

        mContentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI, values);
        mFavourite = true;
        storeTrailersAndReviews();

        storeMoviePosterOnLocalStorage();
    }

    private void storeMoviePosterOnLocalStorage() {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                new PersistRemoteImage(FavouredMovie.this).execute(bitmap, mMovie.getId(), mMovie.getPosterExtension());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        if (!mMovie.posterSavedLocally()) {
            Picasso.with(mActivity).load(mMovie.getPosterPath()).into(target);
        }
    }


    private void deleteMovie() {
        if (mMovie.posterSavedLocally()) {
            final String fileName = mMovie.getId() + "." + mMovie.getPosterExtension();
            mActivity.deleteFile(fileName);
        }
        mContentResolver.delete(MoviesContract.MovieEntry.buildMovieUri(mMovie.getId()), null, null);
        mFavourite = false;
    }

    private void storeTrailersAndReviews() {
        storeTrailers();
        storeReviews();
    }

    private void storeTrailers() {
        if (mMovie.trailersSet()) {
            Trailer[] trailers = mMovie.getTrailers();
            ContentValues[] values = new ContentValues[trailers.length];
            for (int i = 0; i < trailers.length; i++) {
                values[i] = new ContentValues();
                values[i].put(MoviesContract.TrailerEntry.COLUMN_MOVIE_ID, mMovie.getId());
                values[i].put(MoviesContract.TrailerEntry.COLUMN_SOURCE, trailers[i].getSource());
                values[i].put(MoviesContract.TrailerEntry.COLUMN_TITLE, trailers[i].getTitle());
            }
            mContentResolver.bulkInsert(MoviesContract.TrailerEntry.CONTENT_URI, values);
        }
    }

    private void storeReviews() {
        if (mMovie.reviewsSet()) {
            Review[] reviews = mMovie.getReviews();
            ContentValues[] values = new ContentValues[reviews.length];
            for (int i = 0; i < reviews.length; i++) {
                values[i] = new ContentValues();
                values[i].put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, reviews[i].getBody());
                values[i].put(MoviesContract.ReviewEntry.COLUMN_BODY, reviews[i].getBody());
                values[i].put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, mMovie.getId());
            }
            mContentResolver.bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI, values);
        }
    }

    public static Movie factory(Cursor cursor) {
        try {
            return new Movie(
                    MovieCursorHelper.getID(cursor),
                    MovieCursorHelper.getTitle(cursor),
                    MovieCursorHelper.getOverview(cursor),
                    MovieCursorHelper.getRating(cursor),
                    MovieCursorHelper.getPosterPath(cursor),
                    MovieCursorHelper.getReleaseDate(cursor)
            );
        } catch (ParseException e) {
            return null;
        }
    }

    public Context getActivity() {
        return mActivity;
    }

    public void updateMoviePoster(String path) {
        ContentValues values = new ContentValues();
        values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, path);
        mContentResolver.update(MoviesContract.MovieEntry.buildMovieUri(mMovie.getId()), values, null, null);
        mMovie.setPosterPath(path);
    }
}
