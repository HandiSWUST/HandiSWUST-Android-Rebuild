package org.shirakawatyu.swust.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import org.shirakawatyu.swust.MainActivity;
import org.shirakawatyu.swust.R;
import org.shirakawatyu.swust.utils.CourseUtils;
import org.shirakawatyu.swust.utils.DateUtils;

/**
 * Implementation of App Widget functionality.
 */
public class CourseWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // 创建适配器
        Intent adapter = new Intent(context, CourseViewService.class);
        adapter.setData(Uri.fromParts("content", appWidgetId +Integer.toString((int)(Math.random()*10000)),null));
        // 创建小部件的RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.course_widget);
        // 点击文字打开app
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.textView, pendingIntent);
        // 设置Adapter
        views.setRemoteAdapter(R.id.course_list, adapter);
        views.setEmptyView(R.id.course_list, android.R.id.empty);
        // 设置课表数据
        SharedPreferences courses = context.getSharedPreferences("courses", Context.MODE_PRIVATE);
        String weekCourses = courses.getString("week_courses", "[]");
        try {
            JSONArray objects = JSON.parseArray(weekCourses);
            JSONArray todayCourse = CourseUtils.getTodayCourse(objects);
            SharedPreferences.Editor edit = courses.edit();
            edit.putString("today_courses", JSON.toJSONString(todayCourse));
            edit.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
//        Toast.makeText(context, "用户将widget添加桌面了", Toast.LENGTH_SHORT).show();
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            // Instruct the widget manager to update the widget
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals("android.appwidget.action.FORCE_UPDATE")) {
            Log.d("broadcast", "收到广播");
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            onUpdate(context, manager, manager.getAppWidgetIds(new ComponentName(context, CourseWidget.class)));
        }
    }
}