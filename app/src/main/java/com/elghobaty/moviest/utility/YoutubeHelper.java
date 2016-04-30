package com.elghobaty.moviest.utility;

import android.net.Uri;

public class YoutubeHelper {

    public static Uri getURL(String source) {
        return Uri.parse("https://youtu.be/").buildUpon().appendPath(source).build();
    }

    public static Uri getSearchURL(String title) {
        return Uri.parse("https://www.youtube.com/results").buildUpon().appendPath("results").appendQueryParameter("search_query", title + " trailer").build();
    }

}
