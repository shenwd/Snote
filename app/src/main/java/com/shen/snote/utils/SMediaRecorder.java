package com.shen.snote.utils;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by shen on 2017/3/12.
 */

public class SMediaRecorder extends MediaRecorder {

    public static final int RECORD_NULL = 0;
    public static final int RECORD_PREPARE = 1;
    public static final int RECORD_START = 2;
    public static final int RECORD_STOP = 3;
    public static final int RECORD_PAUSE = 4;
    public static final int RECORD_RESUME = 5;
    public static final int RECORD_RELEASE = 6;
    public static final int RECORD_RESET = 6;

    private int recordState = RECORD_NULL;//录音的状态

    public int getRecordState() {
        return recordState;
    }

    public void setRecordState(int recordState) {
        this.recordState = recordState;
    }

    public SMediaRecorder() {

    }

    @Override
    public void prepare() throws IllegalStateException, IOException {
        super.prepare();
        setRecordState(RECORD_PREPARE);
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        setRecordState(RECORD_START);
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        setRecordState(RECORD_STOP);
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        setRecordState(RECORD_PAUSE);
    }

    @Override
    public void resume() throws IllegalStateException {
        super.resume();
        setRecordState(RECORD_RESUME);
    }

    @Override
    public void reset() {
        super.reset();
        setRecordState(RECORD_RESET);
    }

    @Override
    public void release() {
        super.release();
        setRecordState(RECORD_RELEASE);
    }



}
