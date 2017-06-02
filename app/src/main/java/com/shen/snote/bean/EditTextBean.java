package com.shen.snote.bean;

import java.util.List;

/**
 * Created by shen on 2017/3/18.
 */

public class EditTextBean extends IEditNote{

    /**
     * 笔记类型
     * */
    private int noteMode;


    private List<IEditNote> noteList;

    public EditTextBean() {
    }

    public int getNoteMode() {
        return noteMode;
    }

    public void setNoteMode(int noteMode) {
        this.noteMode = noteMode;
    }

    public List<IEditNote> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<IEditNote> noteList) {
        this.noteList = noteList;
    }
}
