package com.shen.snote.bean;

import java.util.List;

/**
 * Created by shen on 2017/3/18.
 */

public class EditTextBean extends IEditNote{

    private int txtMode;//1为普通文本；2为待办文本；3为排序文本
    private boolean isWait;//待办文本标记   true为待办，false为已完成
    private String txt;//文本内容
    public static final int EDIT_NORMAL = 1;

    public EditTextBean(int mode) {
        super.mode = mode;
    }

    public EditTextBean(int mode,int txtMode, String txt) {
        super.mode = mode;
        this.txtMode = txtMode;
        this.txt = txt;
    }

    public int getTxtMode() {
        return txtMode;
    }

    public void setTxtMode(int txtMode) {
        this.txtMode = txtMode;
    }

    public boolean isWait() {
        return isWait;
    }

    public void setWait(boolean wait) {
        isWait = wait;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    private List<IEditNote> noteList;

    public List<IEditNote> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<IEditNote> noteList) {
        this.noteList = noteList;
    }
}
