package com.shen.snote.bean;

/**
 * Created by shen on 2017/3/17.
 * 布局的总接口
 */

public abstract class IEditNote {

//    用于mode
    public static final int IEDITNOTE_EDITTEXT = 1;
    public static final int IEDITNOTE_IMG = 1;
    public static final int IEDITNOTE_RECORDER = 1;

    /**
     * mode = 1;为SEditText;mode = 2,为ImgView；mode = 3,为 recorder
     * */
    protected int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
