package com.shen.snote.view;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.shen.snote.CompileActivity;
import com.shen.snote.R;
import com.shen.snote.RecordActivity;
import com.shen.snote.utils.DensityUtils;
import com.shen.snote.utils.FileUtils;
import com.shen.snote.utils.SMediaRecorder;
import com.shen.snote.utils.SdCardUtils;
import com.shen.snote.utils.TimeUtils;
import com.shen.snote.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by shen on 2017/3/14.
 */

public class DialogRecorder extends BaseDialog {



    public static final String RECORD_REMARK = "record_remark";
    public static final String RECORD_NAME = "record_name";
    public static final String RECORD_DURATION = "record_duration";

    private TextView tvCancel;
    private TextView tvOk;
    private FrameLayout flRecord;
    private EditText etRecordRemark;
    private ImageView ivPlay;
    private ImageView ivPause;
    private TextView tvRecordTime;

    private SMediaRecorder mediaRecorder;
    private String recordName;//录音文件的名字
    //    private MediaPlayer mediaPlayer;
    private Intent returnIntent;//用来返回数据的intent
    private Bundle returnBundle;//用来返回的数据

    private File recordItemFile;//录音临时子文件，待合并的
    private File recordDir;//保存所有录音文件的文件夹
    private File recordItemDir;//以录音文件为名的文件夹,保存单一录音文件

    private int recordState = 0;//录音状态
    private int recordNameSuffic = 1;//录音文件后缀；用于暂停继续录音时 的文件保存
    private Handler handler;
    private Runnable runTime;
    private boolean recordPlay = true;
    private Animation hideAnim;
    private Animation showAnim;

    private Context context;
    public DialogRecorder(Context context) {
        super(context);
        this.context = context;
    }

    public DialogRecorder(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void init() {
        super.init();

        RecorderView recorderView = new RecorderView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(DensityUtils.dp2px(context, 100), DensityUtils.dp2px(context, 100));
        recorderView.setLayoutParams(layoutParams);

       /* ViewGroup.LayoutParams layoutParams = recorderView.getLayoutParams();
        layoutParams.height = DensityUtils.dp2px(context,100);
        layoutParams.width = DensityUtils.dp2px(context,100);
        recorderView.setLayoutParams(layoutParams);*/
        setContentView(recorderView);

        tvCancel = (TextView) findViewById(R.id.tv_record_cacel);
        tvOk = (TextView) findViewById(R.id.tv_record_ok);
        flRecord = (FrameLayout) findViewById(R.id.fl_record);
        etRecordRemark = (EditText) findViewById(R.id.et_record_remark);
        tvRecordTime = (TextView) findViewById(R.id.tv_record_time);
        ivPause = (ImageView) findViewById(R.id.iv_pause_play);
        ivPlay = (ImageView) findViewById(R.id.iv_record_play);

    }

    @Override
    public int getLayoutId() {
        return R.layout.dialog_record;
    }

    private void initData() {
        handler = new Handler();
        mediaRecorder = new SMediaRecorder();
//        mediaPlayer = new MediaPlayer();
        returnIntent = new Intent();
        returnBundle = new Bundle();

        initAnim();

        flRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flRecord.setClickable(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flRecord.setClickable(true);
                    }
                }, 700);
                if (mediaRecorder.getRecordState() == SMediaRecorder.RECORD_START) {
                    ToastUtils.showShort(getContext(), "暂停");
                    mediaRecorder.stop();
                    recordPlay = false;
                    ivPlay.startAnimation(showAnim);
                    ivPause.startAnimation(hideAnim);
                } else {
                    ToastUtils.showShort(getContext(), "开始");
                    recordNameSuffic++;
                    checkFile(recordName, "_" + recordNameSuffic);
                    recordPlay = true;
                    startTimeShow();
                    ivPause.startAnimation(showAnim);
                    ivPlay.startAnimation(hideAnim);
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaRecorder.getRecordState() != SMediaRecorder.RECORD_STOP)
                    mediaRecorder.stop();
                deleteRecordFile(recordName);
                dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mediaRecorder.getRecordState() != SMediaRecorder.RECORD_STOP)
                    mediaRecorder.stop();

                mergeFile(recordItemDir);

                String duration = tvRecordTime.getText().toString();
                returnBundle.putString(RECORD_DURATION, duration);
                returnBundle.putString(RECORD_REMARK, etRecordRemark.getText().toString());
                returnBundle.putString(RECORD_NAME, recordName);
                returnIntent.putExtras(returnBundle);

                dismiss();
            }
        });

        startTimeShow();
    }

    /**
     * 合并音频文件
     *
     * @param// recordItemDir 文件夹
     */
    private File mergeFile(File recordItemDir) {
        if(!recordItemDir.exists()){
            return null;
        }
        File[] files = recordItemDir.listFiles();
        String[]  fileItemsPaths = new String[files.length];
        for(int i = 1;i<=files.length;i++){
            File file = new File(recordItemDir,recordName+"_"+i);
            if(file.exists()){
                fileItemsPaths[i-1] = file.getAbsolutePath();
            }
        }
        File recordFullFile = new File(recordItemDir,recordName);
        if(!recordFullFile.exists()){
            try {
                recordFullFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileUtils.uniteAMRFile(fileItemsPaths,recordFullFile.getAbsolutePath());
        FileUtils.deleteAllFile(fileItemsPaths);
        return null;
    }
    private void initAnim() {
        ivPause.setAlpha(1.0f);
        hideAnim = new AlphaAnimation(1.0f, 0.0f);
        hideAnim.setDuration(700);
        hideAnim.setFillAfter(true);
        showAnim = new AlphaAnimation(0.0f, 1.0f);
        showAnim.setDuration(700);
        showAnim.setFillAfter(true);
        Animation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(10);
        anim.setFillAfter(true);
        ivPlay.startAnimation(anim);
    }

    private int recordTime = 0;

    /**
     * 实时显示录音的时间
     */
    private void startTimeShow() {

        runTime = new Runnable() {
            @Override
            public void run() {
                if (recordPlay) {
                    handler.postDelayed(runTime, 1000);
                    recordTime++;
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvRecordTime.setText(TimeUtils.DurationToTime(recordTime * 1000));
                        }
                    });
                }
            }
        };
        handler.postDelayed(runTime, 1000);

    }

    /**
     * 删除录音文件
     *
     * @param recordName 录音文件名字
     */
    private void deleteRecordFile(String recordName) {
        if (!recordDir.exists()) {
            ToastUtils.showShort(getContext(), "录音文件夹不存在");
            return;
        }
        File file = new File(recordDir, recordName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 检查必要文件夹，并创建需要的文件夹以及文件
     */
    private void checkFile(String recordName, String suffic) {
        if (recordName != null && !TextUtils.isEmpty(recordName)) {
            if (SdCardUtils.isSDCardEnable()) {
                String sdCardPath = SdCardUtils.getSDCardPath();
                File snoteFile = new File(sdCardPath, "snote");
                if (!snoteFile.exists()) {
                    snoteFile.mkdirs();
                }

                recordDir = new File(snoteFile, "record");
                if (!recordDir.exists()) {
                    recordDir.mkdirs();
                }

                recordItemDir = new File(recordDir, recordName);
                if (!recordItemDir.exists()) {
                    recordItemDir.mkdirs();
                }

                recordItemFile = new File(recordItemDir, recordName + suffic);
                if (recordItemFile.exists()) {
                    recordItemFile.delete();
                }
                try {
                    recordItemFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startRecord(recordItemFile.getAbsolutePath());
            } else {
                ToastUtils.showShort(getContext(), "没有SD卡");
            }
        } else {
            ToastUtils.showShort(getContext(), "无效路径");
        }
    }

    private final int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050,16000,11025  
    private final int sampleRateInHz = 16000;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道  
    private final int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。  
    private final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    private  int inBuffSize = 0;

    private void startRecord(String filePath) {
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
        /**
         * 设置录音之后，保存音频文件的位置
         */
        mediaRecorder.setOutputFile(filePath);

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

}
