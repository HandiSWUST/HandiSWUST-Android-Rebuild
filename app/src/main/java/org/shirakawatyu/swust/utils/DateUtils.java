package org.shirakawatyu.swust.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        return Integer.toString(Calendar.DAY_OF_WEEK);
    }
}
