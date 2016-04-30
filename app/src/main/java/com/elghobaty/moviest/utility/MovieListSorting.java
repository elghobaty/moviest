package com.elghobaty.moviest.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.elghobaty.moviest.R;

import java.util.HashMap;

public class MovieListSorting {

    private static final String SORTING_MODE_KEY = "sort_by";
    private static final String TOP_RATED_KEY = "top_rated";
    private static final String POPULAR_KEY = "popular";
    private static final String FAVOURITE_KEY = "favourites";
    private static final String DEFAULT_KEY = TOP_RATED_KEY;
    private static Context mContext;
    private static String[] mSpinnerTextArray;
    private static HashMap<String, String> mSpinnerTextKeyMap;
    private static HashMap<String, Integer> mSpinnerKeyIndexMap;
    private static SharedPreferences sharedPreferences;
    private static String mSortingOrderCached;

    public static boolean isFavourites() {
        String mode = getSortingMode();
        return mode != null && mode.equals(FAVOURITE_KEY);
    }

    public static boolean isTopRated() {
        String mode = getSortingMode();
        return mode != null && mode.equals(TOP_RATED_KEY);
    }

    public static boolean isPopular() {
        String mode = getSortingMode();
        return mode != null && mode.equals(POPULAR_KEY);
    }


    public static String getSortingMode() {
        if ( mSortingOrderCached == null ) {
            mSortingOrderCached = sharedPreferences.getString(SORTING_MODE_KEY, DEFAULT_KEY);
        }
        return mSortingOrderCached;
    }

    public static void setContext(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        prepareSpinnerData();
    }

    private static void prepareSpinnerData() {
        mSpinnerTextArray = new String[]{
                mContext.getString(R.string.top_rated),
                mContext.getString(R.string.popular),
                mContext.getString(R.string.favourite)
        };

        String[] mSpinnerKeyArray = new String[]{
                TOP_RATED_KEY,
                POPULAR_KEY,
                FAVOURITE_KEY,
        };

        mSpinnerTextKeyMap = new HashMap<>();
        mSpinnerKeyIndexMap = new HashMap<>();
        for (int i = 0; i < mSpinnerKeyArray.length; i++) {
            mSpinnerTextKeyMap.put(mSpinnerTextArray[i], mSpinnerKeyArray[i]);
            mSpinnerKeyIndexMap.put(mSpinnerKeyArray[i], i);
        }
    }

    public static String[] getListText() {
        return mSpinnerTextArray;
    }

    public static int getCurrentPosition() {
        return mSpinnerKeyIndexMap.get(getSortingMode());
    }

    public static void updatePreferenceByValue(String spinnerText) {
        String mode = mSpinnerTextKeyMap.get(spinnerText);
        updatePreferenceByKey(mode);
    }

    public static void resetDefaultPreference() {
        updatePreferenceByKey(DEFAULT_KEY);
    }

    private static void updatePreferenceByKey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SORTING_MODE_KEY, key);
        editor.apply();
        mSortingOrderCached = key;
    }
}
