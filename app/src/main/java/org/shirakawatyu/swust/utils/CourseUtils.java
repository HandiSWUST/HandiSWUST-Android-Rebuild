package org.shirakawatyu.swust.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.shirakawatyu.swust.entity.Course;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseUtils {
    public static JSONArray getTodayCourse(JSONArray array) {
        JSONArray jsonArray = new JSONArray();
        String weekDay = DateUtils.getWeekDay();
        if(array != null) {
            for (int i = 0; i < array.size(); i++) {
                if(array.getJSONObject(i).get("week_day").equals(DateUtils.getWeekDay())) {
                    jsonArray.add(array.getJSONObject(i));
                }
            }
        }
        if(jsonArray.size() == 0) {
            Course course = new Course("今天没有课哦", "好好休息吧", "0-0", 0);
            jsonArray.add(JSON.parseObject(JSON.toJSONString(course)));
        }
        return jsonArray;
    }
    public static List<Course> toCourseList(String jsonString) {
        ArrayList<Course> courses = new ArrayList<>();
        if("".equals(jsonString)) {
            courses.add(new Course("今天没有课哦", "好好休息吧", "0-0", 0));
        }else {
            try {
                JSONArray objects = JSON.parseArray(jsonString);
                for (int i = 0; i < objects.size(); i++) {
                    JSONObject jsonObject = objects.getJSONObject(i);
                    courses.add(new Course(
                            jsonObject.getString("jw_course_name"),
                            jsonObject.getString("base_room_name"),
                            jsonObject.getString("section_start") + "-" + jsonObject.getString("section_end"),
                            Integer.parseInt(jsonObject.getString("section_start"))
                    ));
                }
                Collections.sort(courses);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return courses;
    }
}
