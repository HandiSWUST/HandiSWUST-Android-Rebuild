package org.shirakawatyu.swust.utils;

import java.util.Calendar;
import java.util.TimeZone;


public class DateUtils {
    public static String getWeekDay() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int i = c.get(Calendar.DAY_OF_WEEK);
        int week = i - 1;
        if(week == 0) {
            week = 7;
        }
        return Integer.toString(week);
    }
}
