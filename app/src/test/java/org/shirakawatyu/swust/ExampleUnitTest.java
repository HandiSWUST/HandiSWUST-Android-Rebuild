package org.shirakawatyu.swust;

import org.junit.Test;
import org.shirakawatyu.swust.utils.DateUtils;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String weekDay = DateUtils.getWeekDay();
        System.out.println(weekDay);
    }
}