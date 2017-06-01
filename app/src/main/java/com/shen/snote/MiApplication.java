package com.shen.snote;

import android.app.Application;
import android.content.Context;

import com.shen.snote.db.DbExecUtils;

/**
 * Created by shen on 2017/3/2.
 */

public class MiApplication extends Application{

    private static Context context;
    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        DbExecUtils.getInstance();//初始化数据库数据，建表

    }
}
