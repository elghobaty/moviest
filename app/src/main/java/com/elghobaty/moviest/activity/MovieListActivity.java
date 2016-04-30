package com.elghobaty.moviest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;


import com.elghobaty.moviest.adapter.FavouriteMoviePosterAdapter;
import com.elghobaty.moviest.adapter.MoviePosterAdapter;
import com.elghobaty.moviest.adapter.RemoteMoviePosterAdapter;
import com.elghobaty.moviest.task.FetchMovies;
import com.elghobaty.moviest.fragment.MovieDetailFragment;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.data.model.Movie;
import com.elghobaty.moviest.data.MoviesContract;
import com.elghobaty.moviest.utility.DateHelper;
import com.elghobaty.moviest.utility.MovieListSorting;


public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private static final int MOVIE_LOADER = 0;

    public static final String DETAIL_FRAGMENT_TAG = "MOVIE_DETAILS";

    private static final String LAST_SORTING_ORDER_KEY = "LAST_SORTING_ORDER";
    private static final String FETCHED_MOVIES_KEY = "FETCHED_MOVIE_LIST";
    private static final String FIRST_VISIBLE_POSITION_KEY = "FIRST_VISIBLE_POSITION";

    private boolean mTwoPane;
    private boolean mIsDisplayingFavourites;
    private String mLastSortingOrder;

    private GridView mPosterGridView;
    private TextView mNoMoviesTextView;

    private ProgressDialog mLoading;

    private MoviePosterAdapter mMoviePosterAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());


        MovieListSorting.setContext(this);
        DateHelper.setContext(this);

        detectTwoPaneMode();
        setUpMoviePosterGridView();
        setUpEmptyMoviePosterGridViewPlaceHolder();
        setUpMoviePosterAdapter();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        createSpinner(menu.findItem(R.id.sorting_spinner));

        return true;
    }


    @Override
    public void onPause() {
        super.onPause();
        dismissLoadingDialog();
        mLoading = null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateMoviePosterGridIfNecessary();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!MovieListSorting.isFavourites()) {

            Movie[] movies = mMoviePosterAdapter.getMovies();

            if (movies != null) {
                outState.putSerializable(FETCHED_MOVIES_KEY, movies);
                outState.putInt(FIRST_VISIBLE_POSITION_KEY, mPosterGridView.getFirstVisiblePosition());
            }

            if (mLastSortingOrder != null) {
                outState.putString(LAST_SORTING_ORDER_KEY, mLastSortingOrder);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(LAST_SORTING_ORDER_KEY)) {
                mLastSortingOrder = savedInstanceState.getString(LAST_SORTING_ORDER_KEY);
            }

            if (savedInstanceState.containsKey(FETCHED_MOVIES_KEY)) {
                Movie[] movies = (Movie[]) savedInstanceState.getSerializable(FETCHED_MOVIES_KEY);

                displayRemoteMovies();
                updateRemoteMovieList(movies);

                if (savedInstanceState.containsKey(FIRST_VISIBLE_POSITION_KEY)) {
                    mPosterGridView.setSelection(savedInstanceState.getInt(FIRST_VISIBLE_POSITION_KEY));
                }
            }

        }
    }


    private void createLoadingDialog() {
        mLoading = new ProgressDialog(this, R.style.AlertDialogTheme);
        mLoading.setMessage(getString(R.string.loading));
        mLoading.setIndeterminate(false);
        mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoading.setCancelable(false);
    }


    public void showLoadingDialog() {
        if (mLoading == null) {
            createLoadingDialog();
        }

        if (!mLoading.isShowing())
            mLoading.show();
    }


    public void dismissLoadingDialog() {
        if ((mLoading != null) && mLoading.isShowing())
            mLoading.dismiss();
    }


    private void setUpEmptyMoviePosterGridViewPlaceHolder() {
        mNoMoviesTextView = (TextView) findViewById(R.id.no_movies);
        assert mNoMoviesTextView != null;
        mNoMoviesTextView.setVisibility(View.GONE);

        mPosterGridView.setEmptyView(mNoMoviesTextView);

        mNoMoviesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsDisplayingFavourites) {
                    new FetchMovies(MovieListActivity.this).execute();
                }
            }
        });

        updateNoMoviesPlaceholderText();
    }


    private void updateNoMoviesPlaceholderText() {
        int stringId = MovieListSorting.isFavourites() ? R.string.no_favourites : R.string.no_remote_movies;
        mNoMoviesTextView.setText(stringId);
    }


    public void updateRemoteMovieList(Movie[] movies) {
        mMoviePosterAdapter.setMovies(movies);
        ((RemoteMoviePosterAdapter) mMoviePosterAdapter.getAdapter()).notifyDataSetChanged();
    }


    private void displayFavouriteMovies() {
        mIsDisplayingFavourites = true;
        mMoviePosterAdapter.displayFavourites();
        refreshMoviePosterGridAdapter();
    }


    private void refreshMoviePosterGridAdapter() {
        mPosterGridView.setAdapter(mMoviePosterAdapter.getAdapter());
    }


    private void displayRemoteMovies() {
        mIsDisplayingFavourites = false;
        mMoviePosterAdapter.displayRemote();
        refreshMoviePosterGridAdapter();
    }


    private void setUpMoviePosterAdapter() {
        mMoviePosterAdapter = new MoviePosterAdapter(this);
    }


    private void detectTwoPaneMode() {
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
    }


    private void setUpMoviePosterGridView() {
        mPosterGridView = (GridView) findViewById(R.id.movie_list);

        mPosterGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mMoviePosterAdapter.get(position);
                displayMovieDetails(movie);
            }
        });
    }


    private void displayMovieDetails(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();

            arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, movie);
            arguments.putBoolean(MovieDetailFragment.ARG_TWO_PANE, true);
            arguments.putBoolean(MovieDetailFragment.ARG_FAVOURITE_LIST, mIsDisplayingFavourites);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG).commit();

        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);

            intent.putExtra(MovieDetailFragment.ARG_MOVIE, movie);
            intent.putExtra(MovieDetailFragment.ARG_TWO_PANE, false);
            intent.putExtra(MovieDetailFragment.ARG_FAVOURITE_LIST, mIsDisplayingFavourites);

            startActivity(intent);
        }
    }


    private void createSpinner(MenuItem spinnerMenuItem) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                MovieListSorting.getListText()
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinnerMenuItem);

        spinner.setAdapter(adapter);
        spinner.setSelection(MovieListSorting.getCurrentPosition());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerText = spinner.getItemAtPosition(position).toString();
                MovieListSorting.updatePreferenceByValue(spinnerText);
                updateMoviePosterGridIfNecessary();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                MovieListSorting.resetDefaultPreference();
                updateMoviePosterGridIfNecessary();
            }
        });
    }


    private void updateMoviePosterGridIfNecessary() {
        if (!isNecessaryToUpdateMoviePosterGrid()) {
            return;
        }

        mLastSortingOrder = MovieListSorting.getSortingMode();

        if (mTwoPane) {
            destroyDetailFragment();
        }

        updateMoviePosterGrid();
    }

    private void destroyDetailFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private boolean isNecessaryToUpdateMoviePosterGrid() {
        return mLastSortingOrder == null || !mLastSortingOrder.equals(MovieListSorting.getSortingMode());
    }


    private void updateMoviePosterGrid() {
        if (MovieListSorting.isFavourites()) {
            getSupportLoaderManager().restartLoader(MOVIE_LOADER, null, this);
            displayFavouriteMovies();
        } else {
            displayRemoteMovies();
            new FetchMovies(this).execute();
        }

        updateNoMoviesPlaceholderText();
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesContract.MovieEntry.COLUMN_FAVOURED_AT + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (mMoviePosterAdapter.getAdapter() instanceof FavouriteMoviePosterAdapter) {
            ((FavouriteMoviePosterAdapter) mMoviePosterAdapter.getAdapter()).swapCursor((Cursor) data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (mMoviePosterAdapter.getAdapter() instanceof FavouriteMoviePosterAdapter) {
            ((FavouriteMoviePosterAdapter) mMoviePosterAdapter.getAdapter()).swapCursor(null);
        }
    }
}
