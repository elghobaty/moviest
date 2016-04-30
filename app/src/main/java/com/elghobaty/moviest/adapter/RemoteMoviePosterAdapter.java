package com.elghobaty.moviest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.data.model.Movie;
import com.squareup.picasso.Picasso;


public class RemoteMoviePosterAdapter extends BaseAdapter {

    private final Context mContext;

    private Movie[] mMovies;

    public RemoteMoviePosterAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mMovies == null ? 0 : mMovies.length;
    }

    @Override
    public Movie getItem(int position) {
        return mMovies[position];
    }

    @Override
    public long getItemId(int position) {
        return mMovies[position].getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView posterImageView;
        if (convertView == null) {
            posterImageView = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.movie_list_content, parent, false);
        } else {
            posterImageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(getItem(position).getPosterPath()).into(posterImageView);
        return posterImageView;
    }

    public void setMovies(Movie[] movies) {
        mMovies = movies;
    }

    public Movie[] getMovies() {
        return mMovies;
    }
}
