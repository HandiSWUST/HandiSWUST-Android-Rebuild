package org.shirakawatyu.swust.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.shirakawatyu.swust.R;
import org.shirakawatyu.swust.entity.Course;
import org.shirakawatyu.swust.utils.CourseUtils;

import java.util.ArrayList;
import java.util.List;

public class CourseViewService extends RemoteViewsService {

    private class RemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private Intent intent;
        private List<Course> courseList;

        public RemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            this.intent = intent;
        }

        @Override
        public void onCreate() {
            courseList = new ArrayList<>();
        }

        @Override
        public void onDataSetChanged() {
            SharedPreferences courses = context.getSharedPreferences("courses", MODE_PRIVATE);
            String today_courses = courses.getString("today_courses", "");
            courseList = CourseUtils.toCourseList(today_courses);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return courseList.size();
        }

        // 更新子布局RemoteView
        @Override
        public RemoteViews getViewAt(int position) {
            Course course = courseList.get(position);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.item_course);
            remoteViews.setTextViewText(R.id.no, course.getTime());
            remoteViews.setTextViewText(R.id.course_name, course.getJw_course_name());
            remoteViews.setTextViewText(R.id.location, course.getBase_room_name());
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory(this, intent);
    }
}
