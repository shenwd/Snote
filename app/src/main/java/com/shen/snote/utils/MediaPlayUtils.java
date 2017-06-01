package com.shen.snote.utils;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by shen on 2017/3/16.
 */

public class MediaPlayUtils {

    private MediaPlayer mediaPlayer;

    private MediaPlayUtils(){}

    private static MediaPlayUtils mediaPlayUtils;

    public static MediaPlayUtils getInstance(){

        if(mediaPlayUtils == null){
            synchronized (MediaPlayUtils.class){
                if(mediaPlayUtils == null){
                    mediaPlayUtils = new MediaPlayUtils();
                }
            }
        }
        return mediaPlayUtils;
    }

    private void initMediaPlay(){
        mediaPlayer = new MediaPlayer();

    }

    public void startPlay(String path){
        if(mediaPlayer == null){
            initMediaPlay();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDuration(){
        if(mediaPlayer != null){
            int duration = mediaPlayer.getDuration();
            return duration;
        }
        return 0;
    }

    public void release() {
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
    }

    public void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
