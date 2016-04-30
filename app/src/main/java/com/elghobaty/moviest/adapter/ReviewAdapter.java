package com.elghobaty.moviest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.data.model.Review;

public class ReviewAdapter extends BaseAdapter {

    private final Context mContext;
    private Review[] mReviews;

    public ReviewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mReviews == null ? 0 : mReviews.length;
    }

    @Override
    public Review getItem(int position) {
        return mReviews[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout reviewLinearLayout;

        if (convertView == null) {
            reviewLinearLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.review, parent, false);
        } else {
            reviewLinearLayout = (LinearLayout) convertView;
        }

        Review review = getItem(position);

        ((TextView) reviewLinearLayout.findViewById(R.id.review_text_view)).setText(review.getBody());
        ((TextView) reviewLinearLayout.findViewById(R.id.review_author_text_view)).setText(mContext.getString(R.string.by, review.getAuthor()));

        if (position == getCount() - 1) {
            reviewLinearLayout.findViewById(R.id.review_separator).setVisibility(View.GONE);
        } else {
            reviewLinearLayout.findViewById(R.id.review_separator).setVisibility(View.VISIBLE);
        }

        return reviewLinearLayout;
    }

    public void setReviews(Review[] reviews) {
        mReviews = reviews;
    }
}
