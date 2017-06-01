package com.shen.snote.utils;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by shen on 2017/3/16.
 */

public class WindowUtils {

    public static void setBgAlpha(Activity activity,float alpha){
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        attributes.alpha = alpha;
        activity.getWindow().setAttributes(attributes);
    }

}
