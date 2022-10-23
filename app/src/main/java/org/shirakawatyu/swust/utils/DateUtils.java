package org.shirakawatyu.swust.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
    public static long START_DATE = DateUtils.getDate("2022-8-29");

    public static String curWeek() {
        long cur = (System.currentTimeMillis() - START_DATE) / (1000 * 60 * 60 * 24 * 7) + 1;
        return Long.toString(cur);
    }

    public static long getDate(String source) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(source).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

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
