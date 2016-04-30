package com.elghobaty.moviest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.data.model.Trailer;

public class TrailerAdapter extends BaseAdapter {
    private final Context mContext;
    private Trailer[] mTrailers;

    public TrailerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mTrailers == null ? 0 : mTrailers.length;
    }

    @Override
    public Trailer getItem(int position) {
        return mTrailers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView trailerTitle;
        if (convertView == null) {
            trailerTitle = (TextView) LayoutInflater.from(mContext).inflate(R.layout.trailer, parent, false);
        } else {
            trailerTitle = (TextView) convertView;
        }
        trailerTitle.setText(getItem(position).getTitle());
        return trailerTitle;
    }

    public void setTrailers(Trailer[] trailers) {
        mTrailers = trailers;
    }
}
