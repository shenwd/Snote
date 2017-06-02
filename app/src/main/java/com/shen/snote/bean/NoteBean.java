package com.shen.snote.bean;

/**
 * Created by shen on 2017/6/2.
 */

public class NoteBean {
    /**
     * 笔记类型
     * */
    private int noteMode;

    //笔记文本内容
    private String text;

    public NoteBean() {
    }

    public int getNoteMode() {
        return noteMode;
    }

    public void setNoteMode(int noteMode) {
        this.noteMode = noteMode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
