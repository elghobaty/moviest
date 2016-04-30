package com.elghobaty.moviest.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.data.model.Movie;
import com.elghobaty.moviest.data.utility.FavouredMovie;
import com.elghobaty.moviest.data.utility.MovieCursorHelper;
import com.elghobaty.moviest.utility.PicassoHelper;

public class FavouriteMoviePosterAdapter extends android.support.v4.widget.CursorAdapter {

    public FavouriteMoviePosterAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_list_content, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String posterImagePath = MovieCursorHelper.getPosterPath(cursor);
        PicassoHelper.loadPoster(context, posterImagePath, view);
    }

    public Movie get(int position) {
        Cursor cursor = getCursor();
        Movie movie = null;
        if (cursor.moveToPosition(position)) {
            movie = FavouredMovie.factory(cursor);
        }
        return movie;
    }

}
