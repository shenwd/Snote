package com.shen.snote.utils;

import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by shen on 2017/3/16.
 */

public class RecorderUtils {
    



    private  SMediaRecorder mediaRecorder;


    private final int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050,16000,11025  
    private final int sampleRateInHz = 16000;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    private final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private  int inBuffSize = 0;
    private static RecorderUtils recorderUtils;
    private RecorderUtils(){

    }

    public static RecorderUtils getInstance(){
        if(recorderUtils == null){
            synchronized (RecorderUtils.class){
                if(recorderUtils == null){
                        recorderUtils = new RecorderUtils();
                }
            }
        }
        return recorderUtils;
    }






    /***
     * 此外，还有和MediaRecorder有关的几个参数与方法，我们一起来看一下：
     * sampleRateInHz :音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。
     * 给出的实例是44100、22050、11025但不限于这几个参数。例如要采集低质量的音频就可以使用4000、8000等低采样率
     * <p>
     * channelConfig ：声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
     * <p>
     * recorder.stop();停止录音
     * recorder.reset(); 重置录音 ，会重置到setAudioSource这一步
     * recorder.release(); 解除对录音资源的占用
     */
    private  void initRecord() {
        mediaRecorder = new SMediaRecorder();
        /**
         * mediaRecorder.setAudioSource设置声音来源。
         * MediaRecorder.AudioSource这个内部类详细的介绍了声音来源。
         * 该类中有许多音频来源，不过最主要使用的还是手机上的麦克风，MediaRecorder.AudioSource.MIC
         */
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        /**
         * mediaRecorder.setOutputFormat代表输出文件的格式。该语句必须在setAudioSource之后，在prepare之前。
         * OutputFormat内部类，定义了音频输出的格式，主要包含MPEG_4、THREE_GPP、RAW_AMR……等。
         */
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        /**
         * mediaRecorder.setAddioEncoder()方法可以设置音频的编码
         * AudioEncoder内部类详细定义了两种编码：AudioEncoder.DEFAULT、AudioEncoder.AMR_NB
         */
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

    }

    public  void startRecord(String recordPath){

        if(mediaRecorder == null){
            initRecord();
        }

        File file = FileUtils.checkFile(recordPath,1);
        if(!file.exists()){
            return;
        }

        /**
         * 设置录音之后，保存音频文件的位置
         */
        mediaRecorder.setOutputFile(file.getAbsolutePath());

        /**
         * 调用start开始录音之前，一定要调用prepare方法。
         */
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void stop(){
        if(mediaRecorder != null){
            mediaRecorder.stop();
        }
    }

    public void release(){
        if(mediaRecorder != null){
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


}
