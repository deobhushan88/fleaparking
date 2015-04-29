package edu.sjsu.cmpe295.parket.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
     * Returns a ISO-8601 String representation of time set to the next hour
     */
    public String nextHour() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY + 1));
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return df.format(c.getTime());
    }

    /**
     * Returns a ISO-8601 String representation of time set to the next hour + 1
     */
    public String nextHourPlusOne() {
        Calendar c = Calendar.getInstance();
        c.setTime(currentTime);
        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY + 2));
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return df.format(c.getTime());
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
                .append(startAddition)
                .append(cStart.get(Calendar.MINUTE))
                .append(" ")
                .append(cStart.getDisplayName(Calendar.AM_PM, style, locale))
                .append(" - ")
                .append(cEnd.get(Calendar.HOUR))
                .append(":")
                .append(endAddition)
                .append(cEnd.get(Calendar.MINUTE))
                .append(" ")
                .append(cEnd.getDisplayName(Calendar.AM_PM, style, locale));
        return sb.toString();
    }

    /**
     * Returns a string representing date in format MM-dd-YYYY ,
     * given ISO-8601 String representation
     */
    public String getDateString(String s) {
        Calendar calendar = decodeFromString(s);
        int style = Calendar.LONG;
        Locale locale = Locale.US;
        StringBuilder sb = new StringBuilder(calendar.getDisplayName(Calendar.MONTH, style, locale));
        sb.append(" ").append(calendar.get(Calendar.DATE));
        return sb.toString();
    }

    /**
     *  Returns a string in format HH:mm AM/PM, given the hour and minute values from a TimePicker
     */
    public String pickerTimeToString (int hourOfDay, int minute) {
        StringBuffer sb = new StringBuffer("");
        int hour = (hourOfDay > 12) ? (hourOfDay - 12) : hourOfDay;
        hour = (hour == 0) ? 12 : hour;
        String amPm = (hourOfDay >= 12) ? "PM" : "AM";
        String minuteAddition = (minute < 10) ? "0" : "";
        sb.append(hour)
                .append(":")
                .append(minuteAddition)
                .append(minute)
                .append(" ")
                .append(amPm);
        return sb.toString();
    }

    /**
     *  Returns a string in format HH:mm, suitable for later use in ISO8601
     *  given the hour and minute values from a TimePicker. Using this as a
     *  workaround to make it easy to create ISO8601 strings, need to stop using this
     *  in future and revert to using {@link #pickerTimeToString(int, int)}
     */
    public String pickerTimeToMachineString (int hourOfDay, int minute) {
        StringBuffer sb = new StringBuffer("");
        String hourAddition = (hourOfDay < 10) ? "0" : "";
        String minuteAddition = (minute < 10) ? "0" : "";
        sb.append(hourAddition)
                .append(hourOfDay)
                .append(":")
                .append(minuteAddition)
                .append(minute);
        return sb.toString();
    }

    /**
     *  Returns a string in format yyyy-MM-dd, given values from a DatePicker.
     *  This is suitable for later use in ISO8601 string. Using this as a
     *  workaround to make it easy to create ISO8601 strings, need to stop using this
     *  in future and make a pickerDateToString method instead which outputs more human date string
     */
    public String pickerDateToMachineString (int year, int month, int day) {
        StringBuffer sb = new StringBuffer("");
        String monthAddition = (month < 10) ? "0" : "";
        String dayAddition = (day < 10) ? "0" : "";
        sb.append(year)
                .append("-")
                .append(monthAddition)
                .append(month + 1)
                .append("-")
                .append(dayAddition)
                .append(day);
        return sb.toString();
    }

    /** Utility method to get timezone offset string
     *  From http://stackoverflow.com/a/16680815
     */
    public String getCurrentTimezoneOffset() {

        TimeZone tz = TimeZone.getDefault();
        Calendar cal = GregorianCalendar.getInstance(tz);
        int offsetInMillis = tz.getOffset(cal.getTimeInMillis());

        String offset = String.format("%02d%02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

        return offset;
    }

    /**
     * Returns the given calendar object as an ISO8601 formatted string
     *
     */
    public String getFormattedString(Calendar c) {
        return df.format(c.getTime());
    }

}