package com.shen.snote.utils;

import android.graphics.Paint;

/**
 * Created by shen on 2017/3/7.
 */

public class FontUtils {

    public static int getFontHeight(float fontSize){
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
        return height;
    }

}
