package com.elghobaty.moviest.task;

import android.os.AsyncTask;
import android.widget.Toast;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.fragment.MovieDetailFragment;
import com.elghobaty.moviest.utility.HTTPHelper;
import com.elghobaty.moviest.utility.MovieJSONParser;
import org.json.JSONException;

public class FetchMovieDetails extends AsyncTask<Long, Void, String> implements TheMovieDBAPI {

    private final MovieDetailFragment mFragment;

    public FetchMovieDetails(MovieDetailFragment movieDetailFragment) {
        mFragment = movieDetailFragment;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mFragment.showLoadingDialog();
    }

    @Override
    protected String doInBackground(Long... params) {
        return HTTPHelper.getJSON(TheMovieDBAPIHelper.getDetailRequestURI(params[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        mFragment.dismissLoadingDialog();
        try {
            mFragment.updateTrailerList(MovieJSONParser.parseTrailers(result));
            mFragment.updateReviewList(MovieJSONParser.parseReviews(result));
        } catch (JSONException e) {
            Toast.makeText(mFragment.getContext(), mFragment.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
        }

    }
}
