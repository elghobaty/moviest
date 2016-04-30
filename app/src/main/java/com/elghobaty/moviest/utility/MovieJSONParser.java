package com.elghobaty.moviest.utility;

import com.elghobaty.moviest.data.model.Movie;
import com.elghobaty.moviest.data.model.Review;
import com.elghobaty.moviest.data.model.Trailer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;

public class MovieJSONParser {
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w300";
    private static final String JSON_MOVIE_LIST = "results";
    private static final String JSON_MOVIE_POSTER = "poster_path";
    private static final String JSON_MOVIE_TITLE = "title";
    private static final String JSON_MOVIE_RATING = "vote_average";
    private static final String JSON_MOVIE_OVERVIEW = "overview";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_MOVIE_RELEASE_DATE = "release_date";
    private static final String JSON_REVIEW_LIST = "results";
    private static final String JSON_REVIEW_CONTENT = "content";
    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_VIDEO_LIST = "youtube";
    private static final String JSON_VIDEO_SOURCE = "source";
    private static final String JSON_REVIEWS_KEY = "reviews";
    private static final String JSON_TRAILERS_KEY = "trailers";
    private static final String JSON_TRAILER_TITLE = "name";

    public static Movie[] parse(String jsonStr) throws JSONException, ParseException {

        if (jsonStr == null) {
            return null;
        }

        JSONObject moviesJson = new JSONObject(jsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(JSON_MOVIE_LIST);

        int moviesCount = moviesArray.length();
        Movie[] movies = new Movie[moviesCount];

        for (int i = 0; i < moviesCount; ++i) {
            JSONObject movieJSON = moviesArray.getJSONObject(i);

            int id = movieJSON.getInt(JSON_MOVIE_ID);
            String title = movieJSON.getString(JSON_MOVIE_TITLE);
            String overview = movieJSON.getString(JSON_MOVIE_OVERVIEW);
            String posterPath = getFullPosterPath(movieJSON.getString(JSON_MOVIE_POSTER));
            String releaseDate = movieJSON.getString(JSON_MOVIE_RELEASE_DATE);
            double rating = movieJSON.getDouble(JSON_MOVIE_RATING);

            movies[i] = new Movie(id, title, overview, rating, posterPath, releaseDate);
        }

        return movies;
    }

    private static String getFullPosterPath(String path) {
        return BASE_POSTER_URL + path;
    }

    public static Review[] parseReviews(String jsonStr) throws JSONException {

        if (jsonStr == null) {
            return null;
        }

        JSONObject reviewsJSON = (new JSONObject(jsonStr)).getJSONObject(JSON_REVIEWS_KEY);
        JSONArray reviewsArray = reviewsJSON.getJSONArray(JSON_REVIEW_LIST);

        int reviewsCount = reviewsArray.length();
        Review[] reviews = new Review[reviewsCount];
        for (int i = 0; i < reviewsCount; ++i) {
            JSONObject reviewJSON = reviewsArray.getJSONObject(i);
            reviews[i] = new Review(reviewJSON.getString(JSON_REVIEW_CONTENT), reviewJSON.getString(JSON_REVIEW_AUTHOR));
        }

        return reviews;
    }


    public static Trailer[] parseTrailers(String jsonStr) throws JSONException {
        if (jsonStr == null) {
            return null;
        }

        JSONObject videosJSON = (new JSONObject(jsonStr)).getJSONObject(JSON_TRAILERS_KEY);
        JSONArray trailersArray = videosJSON.getJSONArray(JSON_VIDEO_LIST);

        int trailersCount = trailersArray.length();

        Trailer[] trailers = new Trailer[trailersCount];
        for (int i = 0; i < trailersCount; ++i) {
            JSONObject trailerJSON = trailersArray.getJSONObject(i);
            trailers[i] = new Trailer(trailerJSON.getString(JSON_TRAILER_TITLE), trailerJSON.getString(JSON_VIDEO_SOURCE));
        }

        return trailers;
    }

}