package com.elghobaty.moviest.utility;

import android.content.Context;
import com.elghobaty.moviest.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    private static Context mContext;


    public static Calendar calendarFromYYYYMMdd(String date) throws ParseException {
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateParser.parse(date));
        return calendar;
    }

    public static String getFormattedMonthYear(Calendar calendar) {
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        int year = calendar.get(Calendar.YEAR);
        return mContext.getString(R.string.release_date, month, year);
    }

    public static void setContext(Context context) {
        mContext = context;
    }

    public static String getDateTimeNowString() {
        SimpleDateFormat nowDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return nowDateFormat.format(new Date());

    }
}
