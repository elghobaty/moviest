package com.elghobaty.moviest.data.model;

import com.elghobaty.moviest.utility.DateHelper;
import com.elghobaty.moviest.utility.FileHelper;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;

public class Movie implements Serializable {
    private final long mId;
    private final String mTitle;
    private final String mOverview;
    private final double mRating;
    private String mPosterPath;
    private final String mReleaseDate;
    private Calendar mReleaseDateCalendar;
    private Trailer[] mTrailers = null;
    private Review[] mReviews = null;

    public Movie(long id, String title, String overview, double rating,
                 String posterPath, String releaseDate) throws ParseException {
        mId = id;
        mTitle = title;
        mOverview = overview;
        mRating = rating;
        mPosterPath = posterPath;
        mReleaseDate = releaseDate;
        mReleaseDateCalendar = DateHelper.calendarFromYYYYMMdd(mReleaseDate);
    }

    public long getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getRating() {
        return mRating;
    }

    public String getPosterPath() {
        return mPosterPath;
    }


    public String getReleaseMonthAndYear() {
        return DateHelper.getFormattedMonthYear(mReleaseDateCalendar);
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public boolean posterSavedLocally() {
        return FileHelper.isLocalPath(getPosterPath());
    }

    public boolean trailersReviewsSet() {
        return trailersSet() || reviewsSet();
    }

    public Review[] getReviews() {
        return mReviews;
    }

    public Trailer[] getTrailers() {
        return mTrailers;
    }

    public void setTrailers(Trailer[] trailers) {
        mTrailers = trailers;
    }

    public void setReviews(Review[] reviews) {
        mReviews = reviews;
    }

    public boolean trailersSet() {
        return mTrailers != null;
    }

    public boolean reviewsSet() {
        return mReviews != null;
    }

    public String getPosterExtension() {
        return FileHelper.getFileExtension(getPosterPath());
    }

    public void setPosterPath(String path) {
        mPosterPath = path;
    }
}

