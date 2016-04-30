package com.elghobaty.moviest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import com.elghobaty.moviest.fragment.MovieDetailFragment;
import com.elghobaty.moviest.R;

import java.io.Serializable;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();

            Intent intent = getIntent();

            boolean isTwoPane = intent.getBooleanExtra(MovieDetailFragment.ARG_TWO_PANE, false);
            boolean isFavouriteList = intent.getBooleanExtra(MovieDetailFragment.ARG_FAVOURITE_LIST, false);
            Serializable Movie = intent.getSerializableExtra(MovieDetailFragment.ARG_MOVIE);

            arguments.putBoolean(MovieDetailFragment.ARG_TWO_PANE, isTwoPane);
            arguments.putBoolean(MovieDetailFragment.ARG_FAVOURITE_LIST, isFavouriteList);
            arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, Movie);

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment, MovieListActivity.DETAIL_FRAGMENT_TAG)
                    .commit();
        }

    }

}
