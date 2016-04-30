package com.elghobaty.moviest.adapter;

import android.content.Context;
import android.widget.ListAdapter;
import com.elghobaty.moviest.data.model.Movie;

public class MoviePosterAdapter {

    private final RemoteMoviePosterAdapter mRemoteMoviePosterAdapter;
    private final FavouriteMoviePosterAdapter mFavouriteMoviePosterAdapter;
    private boolean mIsDisplayingFavourites;

    public MoviePosterAdapter(Context context) {
        mFavouriteMoviePosterAdapter = new FavouriteMoviePosterAdapter(context);
        mRemoteMoviePosterAdapter = new RemoteMoviePosterAdapter(context);
    }

    public Movie get(int position) {
        if (mIsDisplayingFavourites) {
            return mFavouriteMoviePosterAdapter.get(position);
        }

        return mRemoteMoviePosterAdapter.getItem(position);
    }

    public void displayFavourites() {
        mIsDisplayingFavourites = true;
    }

    public void displayRemote() {
        mIsDisplayingFavourites = false;
    }

    public void setMovies(Movie[] movies) {
        if (mIsDisplayingFavourites) {
            throw new UnsupportedOperationException("setMovies on FavouriteMoviePosterAdapter is not implemented and should not be called.");
        }
        mRemoteMoviePosterAdapter.setMovies(movies);
    }

    public ListAdapter getAdapter() {
        return mIsDisplayingFavourites? mFavouriteMoviePosterAdapter : mRemoteMoviePosterAdapter;
    }

    public Movie[] getMovies() {
        if (mIsDisplayingFavourites) {
            throw new UnsupportedOperationException("Get all movies from FavouriteMoviePosterAdapter is not implemented.");
        }
        return mRemoteMoviePosterAdapter.getMovies();
    }

}
