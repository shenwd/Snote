package com.shen.snote.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shen.snote.MiApplication;
import com.shen.snote.NoteData;
import com.shen.snote.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shen on 2017/3/2.
 */

public class DbExecUtils {

    private DbHelper dbHelper;
    private static DbExecUtils dbExecUtils = null;

    private DbExecUtils() {
        dbHelper = new DbHelper(MiApplication.getContext(), "note_data", 1);
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        dbHelper.close();
    }

    public static DbExecUtils getInstance() {
        if (dbExecUtils == null) {
            synchronized (DbExecUtils.class) {
                if (dbExecUtils == null) {
                    LogUtils.d("data","创建数据库工具类DbExecUtils");
                    dbExecUtils = new DbExecUtils();
                }
            }
        }
        return dbExecUtils;
    }

    /**
     * 插入方法
     * */
    public void insert(ContentValues cv){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        /*ContentValues cv = new ContentValues();
        if(data.get("title")!= null) cv.put("title",data.get("title"));
        if(data.get("text")!= null) cv.put("text",data.get("text"));
        if(data.get("all_sp")!= null) cv.put("all_sp",data.get("all_sp"));
        if(data.get("label")!= null) cv.put("label",data.get("label"));
        if(data.get("alert")!= null) cv.put("alert",data.get("alert"));
        if(data.get("last_edit_time")!= null) cv.put("last_edit_time",data.get("last_edit_time"));
        if(data.get("frist_edit_time")!= null) cv.put("frist_edit_time",data.get("frist_edit_time"));*/
        writableDatabase.insert("notedb","title",cv);
        writableDatabase.close();

    }

    public void updata(int id,ContentValues cv){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.update(dbName,cv,"id = ?",new String[]{String.valueOf(id)});
        writableDatabase.close();
    }

    private String dbName = "notedb";
    public String query(String column ,int id){
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
//        String[] columns = new String[]{};
//        String selection = "";
//        String[] selectionArgs = new String[]{};
//        String groupBy = "";
//        String having = "";
//        String orderBy = "";
//        Cursor query = readableDatabase.query(dbName, columns, selection, selectionArgs, groupBy, having, orderBy);
        String sql = "select "+ column +" from notedb where id = ?";
        Cursor query = readableDatabase.rawQuery(sql, new String[]{String.valueOf(id)});

        String result = null;
        if(query.moveToNext()){
            result = query.getString(0);

        }
        query.close();
        readableDatabase.close();
        return result;
    }

    public List<NoteData> queayAll(){
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();
        String sql = "select * from notedb";
        Cursor query = readableDatabase.rawQuery(sql, null);
        List<NoteData> list = new ArrayList<NoteData>();
        NoteData noteData;
        while(query.moveToNext()){
            noteData = new NoteData();

            String allSp = query.getString(query.getColumnIndex("all_sp"));
            String lastEditTime = query.getString(query.getColumnIndex("last_edit_time"));
            String fristEditTime = query.getString(query.getColumnIndex("frist_edit_time"));
            int id = query.getInt(query.getColumnIndex("id"));

            noteData.setId(id);
            noteData.setAllSp(allSp);
            noteData.setFristEditTime(fristEditTime);
            noteData.setLastEditTime(lastEditTime);

            list.add(noteData);
        }

        return list;
    }
}
