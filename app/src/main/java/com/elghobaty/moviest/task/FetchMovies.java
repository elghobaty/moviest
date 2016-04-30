package com.elghobaty.moviest.task;

import android.os.AsyncTask;
import android.widget.Toast;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.activity.MovieListActivity;
import com.elghobaty.moviest.utility.HTTPHelper;
import com.elghobaty.moviest.utility.MovieJSONParser;
import org.json.JSONException;
import java.text.ParseException;

public class FetchMovies extends AsyncTask<Void, Void, String> implements TheMovieDBAPI {

    private final MovieListActivity mActivity;

    public FetchMovies(MovieListActivity movieListActivity) {
        mActivity = movieListActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mActivity.showLoadingDialog();
    }

    @Override
    protected String doInBackground(Void... params) {
        return HTTPHelper.getJSON(TheMovieDBAPIHelper.getListRequestURI());
    }


    @Override
    protected void onPostExecute(String jsonStr) {
        mActivity.dismissLoadingDialog();
        try {
            mActivity.updateRemoteMovieList(MovieJSONParser.parse(jsonStr));
        } catch (JSONException | ParseException e) {
            Toast.makeText(mActivity, mActivity.getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
        }
    }

}
