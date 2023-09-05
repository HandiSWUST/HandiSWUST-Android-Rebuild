package org.shirakawatyu.swust.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class DateUtils {

    public static String curWeek(Context context) {
        SharedPreferences week = context.getSharedPreferences("week", Context.MODE_PRIVATE);
        new Thread(() -> {
            OkHttpClient httpClient = new OkHttpClient();
            Request request = new Request.Builder().url("http://124.220.158.71:82/api/week").get().build();
            try {
                Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = JSON.parseObject(response.body().string());
                    week.edit().putLong("startDate", jsonObject.getLong("startDate")).apply();
                    week.edit().putInt("total", jsonObject.getIntValue("total")).apply();
                }
            }catch (Exception ignored) {}
        }).start();
        long cur = (System.currentTimeMillis() - week.getLong("startDate", DateUtils.getDate("2022-7-29"))) / (1000 * 60 * 60 * 24 * 7) + 1;
        int total = week.getInt("total", 20);
        if (cur > total) return Integer.toString(total);
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
