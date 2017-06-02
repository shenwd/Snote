package com.shen.snote.bean;

import java.util.List;

/**
 *
 * 保存全部笔记内容
 * Created by shen on 2017/6/2.
 */

public class NoteBeans {

    private List<NoteBean> noteBeens;

    public NoteBeans() {

    }

    public List<NoteBean> getNoteBeens() {
        return noteBeens;
    }

    public void setNoteBeens(List<NoteBean> noteBeens) {
        this.noteBeens = noteBeens;
    }

}
