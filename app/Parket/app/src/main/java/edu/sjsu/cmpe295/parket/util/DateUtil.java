package edu.sjsu.cmpe295.parket.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by bdeo on 4/16/15.
 */
public class DateUtil {
    private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private final TimeZone deviceTimeZone = TimeZone.getDefault();
    private Date currentTime;
    private Date thirtyMinutesFromCurrentTime;
    private final String TAG = "DateUtil";

    public DateUtil() {
        df.setTimeZone(this.deviceTimeZone);
        this.currentTime = new Date();
        this.thirtyMinutesFromCurrentTime = new Date();
        this.thirtyMinutesFromCurrentTime.setTime(thirtyMinutesFromCurrentTime.getTime() + 1800000);
    }

    /**
     * Returns ISO-8601 String representation of current time
     */
    public String now() {
        return df.format(currentTime);
    }

    /**
     * Returns ISO-8601 String representation of current time + 30 minutes
     */
    public String thirtyMinutesFromNow() {
        return df.format(thirtyMinutesFromCurrentTime);
    }

    /**
     * Returns Calendar object with date, time set to the input ISO-8601 String representation
     */
    public Calendar decodeFromString(String d) {
        Date date = null;
        try {
            date = df.parse(d);

        } catch (ParseException e) {
            Log.e(TAG, "Invalid Date, cannot parse", e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Returns a string representing range in format HH:mm - HH:mm ,
     * given two ISO-8601 String representations which denote start and end time
     */
    public String getRangeString(String start, String end) {
        Calendar cStart = decodeFromString(start);
        Calendar cEnd = decodeFromString(end);
        int style = Calendar.LONG;
        Locale locale = Locale.US;

        // handle single digit minute
        String startAddition = (cStart.get(Calendar.MINUTE) < 10) ? "0" : "";
        String endAddition = (cStart.get(Calendar.MINUTE) < 10) ? "0" : "";
        // make the range string
        StringBuffer sb = new StringBuffer("");
        sb.append(cStart.get(Calendar.HOUR))
                .append(":")
                .append(cStart.get(Calendar.MINUTE))
                .append(startAddition)
                .append(" ")
                .append(cStart.getDisplayName(Calendar.AM_PM, style, locale))
                .append(" - ")
                .append(cEnd.get(Calendar.HOUR))
                .append(":")
                .append(cEnd.get(Calendar.MINUTE))
                .append(endAddition)
                .append(" ")
                .append(cEnd.getDisplayName(Calendar.AM_PM, style, locale));
        return sb.toString();
    }

}