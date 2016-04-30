package com.elghobaty.moviest.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.*;
import android.widget.*;
import com.elghobaty.moviest.adapter.ReviewAdapter;
import com.elghobaty.moviest.adapter.TrailerAdapter;
import com.elghobaty.moviest.data.model.Review;
import com.elghobaty.moviest.task.FetchMovieDetails;
import com.elghobaty.moviest.R;
import com.elghobaty.moviest.data.model.Movie;
import com.elghobaty.moviest.data.utility.FavouredMovie;
import com.elghobaty.moviest.data.model.Trailer;
import com.elghobaty.moviest.utility.PicassoHelper;
import com.elghobaty.moviest.utility.YoutubeHelper;

public class MovieDetailFragment extends Fragment {

    public static final String ARG_MOVIE = "movie";
    public static final String ARG_TWO_PANE = "two_pane";
    public static final String ARG_FAVOURITE_LIST = "is_favourite_list";
    private static final String TRAILER_SHARE_TAG = "#Moviest";

    private boolean mTwoPane;
    private boolean mFavouriteList;

    private LinearLayout mReviewsLinearLayout;
    private TextView mNoReviewsTextView;
    private TextView mFavouriteTextView;

    private Intent mShareIntent;
    private ProgressDialog mLoading;

    private Movie mMovie;
    private FavouredMovie mFavouredMovie;
    private TrailerAdapter mTrailersAdapter;
    private ReviewAdapter mReviewsAdapter;


    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_MOVIE)) {
            Activity activity = getActivity();

            mTwoPane = arguments.getBoolean(ARG_TWO_PANE);
            mFavouriteList = arguments.getBoolean(ARG_FAVOURITE_LIST);
            mMovie = (Movie) arguments.getSerializable(ARG_MOVIE);

            mFavouredMovie = new FavouredMovie(activity, mMovie);
        }

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        updateUIWithMovieDetails(rootView);
        setUpFavouring(rootView);
        setUpTrailers(rootView);
        setUpReviews(rootView);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissLoadingDialog();
        mLoading = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mMovie != null) {
            if (mMovie.trailersReviewsSet()) {
                updateTrailerList();
                updateReviewList();
            } else {
                hideTrailers();
                hideReviews();
                new FetchMovieDetails(this).execute((long) mMovie.getId());
            }
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.detail_fragment, menu);
        MenuItem shareItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if (mShareActionProvider == null) {
            return;
        }

        mShareIntent = getFirstTrailerShareIntent();

        if (mShareIntent != null) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }

    private void createLoadingDialog() {
        mLoading = new ProgressDialog(getContext(), R.style.AlertDialogTheme);
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


    private void setUpReviews(View rootView) {
        mReviewsLinearLayout = (LinearLayout) rootView.findViewById(R.id.reviews_list_view);
        mNoReviewsTextView = (TextView) rootView.findViewById(R.id.no_reviews);
        mReviewsAdapter = new ReviewAdapter(getContext());
    }

    private void setUpTrailers(View rootView) {
        TextView noTrailers = (TextView) rootView.findViewById(R.id.no_trailers);
        ListView trailersListView = (ListView) rootView.findViewById(R.id.trailers_list_view);

        mTrailersAdapter = new TrailerAdapter(getContext());

        trailersListView.setAdapter(mTrailersAdapter);
        trailersListView.setEmptyView(noTrailers);

        trailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer trailer = mTrailersAdapter.getItem(position);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailer.getSource()));

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getSource()));
                    startActivity(intent);
                }
            }
        });
    }

    private void setUpFavouring(View rootView) {
        mFavouriteTextView = (TextView) rootView.findViewById(R.id.favourite_text_view);

        fixFavouriteView();

        mFavouriteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavouredMovie.isFavourite()) {
                    mFavouredMovie.UnFavour();
                    if (mFavouriteList && mTwoPane) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(MovieDetailFragment.this).commit();
                    }
                } else {
                    mFavouredMovie.favour();
                }
                fixFavouriteView();
            }
        });
    }


    private void updateUIWithMovieDetails(View rootView) {

        if (mMovie != null) {
            PicassoHelper.loadPoster(getContext(), mMovie.getPosterPath(), rootView.findViewById(R.id.movie_poster_image_view));

            ((TextView) rootView.findViewById(R.id.movie_title_text_view)).setText(mMovie.getTitle());

            ((TextView) rootView.findViewById(R.id.movie_overview_text_view)).setText(mMovie.getOverview());

            ((TextView) rootView.findViewById(R.id.movie_release_year_text_view)).setText(mMovie.getReleaseMonthAndYear());

            String rating = getString(R.string.formatted_rating, mMovie.getRating());
            ((TextView) rootView.findViewById(R.id.movie_rating_text_view)).setText(rating);
        }
    }


    private void fixFavouriteView() {
        if (mFavouredMovie.isFavourite()) {
            UpdateUIToFavouriteMode();
        } else {
            UpdateUIToNonFavouriteMode();
        }
    }


    private Intent getFirstTrailerShareIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getFirstTrailerShareMessage());
        return intent;
    }

    private String getFirstTrailerShareMessage() {
        String message = "";

        if (mMovie != null) {
            Trailer[] trailers = mMovie.getTrailers();

            Uri link;
            if (trailers == null || trailers.length == 0) {
                link = YoutubeHelper.getSearchURL(mMovie.getTitle());
            } else {
                link = YoutubeHelper.getURL(trailers[0].getSource());
            }
            message = getString(R.string.share_message, link, TRAILER_SHARE_TAG);
        }

        return message;
    }


    private void UpdateUIToFavouriteMode() {
        if (mFavouriteTextView != null) {
            mFavouriteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_light_24dp, 0, 0, 0);
            mFavouriteTextView.setText(R.string.un_favour);
        }
    }

    private void UpdateUIToNonFavouriteMode() {
        if (mFavouriteTextView != null) {
            mFavouriteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_light_24dp, 0, 0, 0);
            mFavouriteTextView.setText(R.string.favour);
        }
    }


    private void hideView(int viewResourceId) {
        getActivity().findViewById(viewResourceId).setVisibility(View.GONE);
    }

    private void showView(int viewResourceId) {
        getActivity().findViewById(viewResourceId).setVisibility(View.VISIBLE);
    }


    private void showTrailers() {
        showView(R.id.trailers_divider);
        showView(R.id.trailers_header);
        showView(R.id.trailers_list_view);
    }

    private void hideTrailers() {
        hideView(R.id.trailers_divider);
        hideView(R.id.trailers_header);
        hideView(R.id.trailers_list_view);
    }

    private void showReviews() {
        showView(R.id.reviews_divider);
        showView(R.id.reviews_header);
        showView(R.id.reviews_list_view);
    }

    private void hideReviews() {
        hideView(R.id.reviews_divider);
        hideView(R.id.reviews_header);
        hideView(R.id.reviews_list_view);
    }


    private void updateTrailerList() {
        if (mShareIntent != null) {
            mShareIntent.putExtra(Intent.EXTRA_TEXT, getFirstTrailerShareMessage());
        }
        mTrailersAdapter.setTrailers(mMovie.getTrailers());
        mTrailersAdapter.notifyDataSetChanged();
        showTrailers();
    }

    public void updateTrailerList(Trailer[] trailers) {
        mFavouredMovie.setTrailers(trailers);
        updateTrailerList();
    }

    private void updateReviewList() {
        mReviewsAdapter.setReviews(mMovie.getReviews());
        reviewsChanged();
        showReviews();
    }

    public void updateReviewList(Review[] reviews) {
        mFavouredMovie.setReviews(reviews);
        updateReviewList();
    }


    private void reviewsChanged() {
        mReviewsLinearLayout.removeAllViews();

        int reviewsCount = mReviewsAdapter.getCount();
        if (reviewsCount > 0) {
            mNoReviewsTextView.setVisibility(View.GONE);
            ViewGroup parent = (ViewGroup) mReviewsLinearLayout.getParent();
            for (int i = 0; i < reviewsCount; i++) {
                mReviewsLinearLayout.addView(mReviewsAdapter.getView(i, null, parent));
            }
        } else {
            mNoReviewsTextView.setVisibility(View.VISIBLE);
        }

    }

}
