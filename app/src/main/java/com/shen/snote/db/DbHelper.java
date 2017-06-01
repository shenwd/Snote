package com.shen.snote.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shen.snote.utils.LogUtils;

/**
 * Created by shen on 2017/3/2.
 */

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(Context context,String dbName,int version) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.d("database","开始创建数据库！！！！！");
        db.execSQL("CREATE TABLE IF NOT EXISTS notedb (\n" +
                "  id INTEGER PRIMARY KEY autoincrement, \n" +
                "  title char, \n" +
                "  text char,   \n" +
                "  all_sp char,\n" +
                "  label INTEGER, \n" +
                "  alert char, \n" +
                "  last_edit_time char, \n" +
                "  frist_edit_time char);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
