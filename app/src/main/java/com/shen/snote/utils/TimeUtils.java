package com.shen.snote.utils;

/**
 * Created by shen on 2017/3/7.
 */

public class TimeUtils {

    public static String DurationToTime(int duration){

        int secondCount = duration / 1000;
        int second = secondCount % 60;
        int min = secondCount / 60;

        StringBuilder sbMin = new StringBuilder();
        if(min<10){
            sbMin.append("0");
            sbMin.append(min);
        } else {
            sbMin.append(min);
        }
        sbMin.append(":");

        if(second<10){
            sbMin.append("0");
            sbMin.append(second);
        } else {
            sbMin.append(second);
        }
        return sbMin.toString();
    }
}
