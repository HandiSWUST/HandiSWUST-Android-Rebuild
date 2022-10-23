package org.shirakawatyu.swust.entity;

public class Course implements Comparable<Course>{
    String jw_course_name;
    String base_room_name;
    String time;
    int section_start;
    int section_end;

    public int getSection_end() {
        return section_end;
    }

    public void setSection_end(int section_end) {
        this.section_end = section_end;
    }

    public Course(String jw_course_name, String base_room_name, String time, int section_start) {
        this.jw_course_name = jw_course_name;
        this.base_room_name = base_room_name;
        this.time = time;
        this.section_start = section_start;
        this.section_end = 0;
    }

    public int getSection_start() {
        return section_start;
    }

    public void setSection_start(int section_start) {
        this.section_start = section_start;
    }

    public String getJw_course_name() {
        return jw_course_name;
    }

    public void setJw_course_name(String jw_course_name) {
        this.jw_course_name = jw_course_name;
    }

    public String getBase_room_name() {
        return base_room_name;
    }

    public void setBase_room_name(String base_room_name) {
        this.base_room_name = base_room_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public int compareTo(Course o) {
        if(section_start > o.section_start) {
            return 1;
        }else if(section_start < o.section_start) {
            return -1;
        }
        return 0;
    }
}
