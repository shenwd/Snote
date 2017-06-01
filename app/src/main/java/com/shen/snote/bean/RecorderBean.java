package com.shen.snote.bean;

/**
 * Created by shen on 2017/3/18.
 */

public class RecorderBean extends IEditNote {

    private int duration;//录音时长
    private String time;//录音时的时间
    private String path;//录音的路径

    public RecorderBean(int mode) {
        super.mode = mode;
    }

    public RecorderBean(int mode,int duration, String time, String path) {
        super.mode = mode;
        this.duration = duration;
        this.time = time;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
