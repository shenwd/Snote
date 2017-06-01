package com.shen.snote.bean;

/**
 * Created by shen on 2017/3/18.
 */

public class ImgBean extends IEditNote{

    private String path;//图片路径

    public ImgBean(int mode) {
        super.mode = mode;
    }

    public ImgBean(int mode,String path) {
        super.mode = mode;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
