package com.shen.snote;

import java.util.List;

/**
 * 笔记的内容
 * Created by shen on 2017/3/1.
 */
public class NoteData {

    private int id;//笔记在数据库中的id
    private String allSp;//所有的sp数据
    private String fristEditTime;//第一次编辑的时间
    private String lastEditTime;//最后一次编辑的时间

   /* private List<Note> notes;//不停类型分开顺序记录
    private String title;//标题
    private String label;//便签分类
    private String alert;//提醒闹钟

    private class Note {
        private int noteType;//当条记录的类型
        private String text;//文本记录
        private String imgPath;//图片路径
        private String recordPath;//录音路径

        public Note(int noteType, String text, String imgPath, String recordPath) {
            this.noteType = noteType;
            this.text = text;
            this.imgPath = imgPath;
            this.recordPath = recordPath;
        }

        public int getNoteType() {
            return noteType;
        }

        public void setNoteType(int noteType) {
            this.noteType = noteType;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getRecordPath() {
            return recordPath;
        }

        public void setRecordPath(String recordPath) {
            this.recordPath = recordPath;
        }
    }

    public NoteData(List<Note> notes, String title, String label, String alert, String fristEditTime, String lastEditTime) {
        this.notes = notes;
        this.title = title;
        this.label = label;
        this.alert = alert;
        this.fristEditTime = fristEditTime;
        this.lastEditTime = lastEditTime;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
*/
    public String getFristEditTime() {
        return fristEditTime;
    }

    public void setFristEditTime(String fristEditTime) {
        this.fristEditTime = fristEditTime;
    }

    public String getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(String lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAllSp() {
        return allSp;
    }

    public void setAllSp(String allSp) {
        this.allSp = allSp;
    }
}
