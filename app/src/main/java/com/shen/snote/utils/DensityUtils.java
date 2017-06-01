package com.shen.snote.utils;

import android.content.Context;
import android.util.TypedValue;

import com.shen.snote.MiApplication;

/**
 * Created by shen on 2016/8/19.
 */
public class DensityUtils {

    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *  
     *
     * @param pxValue            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *  
     *
     * @param dipValue
     * @return
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     *  
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     *  
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
