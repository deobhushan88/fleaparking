package edu.sjsu.cmpe295.parket.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bdeo on 4/16/15.
 */
public class DateUtil {
    private DateFormat df;
    private TimeZone deviceTimeZone;
    private Date currentTime;
    private Date thirtyMinutesFromCurrentTime;

    public DateUtil() {
        this.deviceTimeZone = TimeZone.getDefault();
        this.df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        df.setTimeZone(this.deviceTimeZone);
        this.currentTime = new Date();
        this.thirtyMinutesFromCurrentTime = new Date();
        this.thirtyMinutesFromCurrentTime.setTime(thirtyMinutesFromCurrentTime.getTime() + 1800000);
    }

    public DateUtil(String date, String time)
    {
        this.deviceTimeZone = TimeZone.getDefault();

    }

    public String now() {
        return df.format(currentTime);
    }

    public String thirtyMinutesFromNow() {
        return df.format(thirtyMinutesFromCurrentTime);
    }

}
