package com.shen.snote.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shen.snote.R;
import com.shen.snote.utils.RecorderUtils;
import com.shen.snote.utils.ToastUtils;
import com.shen.snote.utils.WindowUtils;

/**
 * Created by shen on 2017/3/14.
 */

public class PopupRecorder extends PopupWindow{

    public static final String RECORD_TIME = "recordTime";
    private Context context;
    private RecorderView recorderView;
    private TranslateAnimation taShowAnim;
    private View view;
    private VoiceLineView voiceView;
    private Handler handler;
    private Runnable runLine;
    private TextView tvTime;
    private TextView tvHind;

    private int maxLength = 60;//录音最大的长度为60s
    private Runnable alphaRun;
    private Runnable alphaRun2;

    public Bundle getRecordBundle() {
        return recordBundle;
    }

    private Bundle recordBundle;//记录数据，回传

    public PopupRecorder(Context context) {
        super(context);
        this.context = context;
        init();
        initData();
    }

    private void init() {
        // TODO: 2016/5/17 设置动画
        this.setAnimationStyle(R.style.popup_window_anim_record_bottom);
        // TODO: 2016/5/17 设置背景颜色
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // TODO: 2016/5/17 设置可以获取焦点
        this.setFocusable(true);
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        this.setOutsideTouchable(false);
        // TODO：更新popupwindow的状态
        this.update();

        view = LayoutInflater.from(context).inflate(R.layout.popup_record, null);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        recorderView = (RecorderView) view.findViewById(R.id.rv);
        voiceView = (VoiceLineView) view.findViewById(R.id.voiceLine);
        tvTime = (TextView) view.findViewById(R.id.tv_time_show);
        tvHind = (TextView) view.findViewById(R.id.tv_popup_hint);

        setContentView(view);

        recorderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){
                    return true;
                }
                return false;
            }
        });
    }

    private void initData() {
        handler = new Handler();
        recordBundle = new Bundle();
    }

    private void runLine(boolean isRun) {
        if (!isRun) {
            if (runLine != null) {
                handler.removeCallbacks(runLine);
            }
            return;
        }

        runLine = new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lineGo();
                    }
                });
                handler.postDelayed(runLine, 1000);
            }
        };
        handler.postDelayed(runLine, 1000);
    }

    private void lineGo() {
//        double ratio = (double) mMediaRecorder.getMaxAmplitude() / 100;
        double ratio = 50;
        double db = 0;// 分贝
        //默认的最大音量是100,可以修改，但其实默认的，在测试过程中就有不错的表现
        //你可以传自定义的数字进去，但需要在一定的范围内，比如0-200，就需要在xml文件中配置maxVolume
        //同时，也可以配置灵敏度sensibility
        if (ratio > 1)
            db = 20 * Math.log10(ratio);
        //只要有一个线程，不断调用这个方法，就可以使波形变化
        //主要，这个方法必须在ui线程中调用
        voiceView.setVolume((int) (db));
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        show();

    }

    /**
     * 用于每次显示初始化
     */
    private void show() {
        recordBundle.clear();
        tvTime.setText("00");
        runTimeShow(true);
        RecorderUtils.getInstance().startRecord(recordName);
        runHintAnim();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recorderView.start();
                        runLine(true);
                    }
                });
            }
        }, 600);

//        设置弹出时背景的动画
        alphaRun2 = new Runnable() {
            @Override
            public void run() {
                alpha -= 0.1;
                if(alpha >=0.2){
                    handler.postDelayed(alphaRun2,50);
                    WindowUtils.setBgAlpha((Activity) context, alpha);
                } else {
                    handler.removeCallbacks(alphaRun2);
                }
            }
        };
        handler.postDelayed(alphaRun2,50);
    }

    /**
     * 提示文字的动画
     */
    private void runHintAnim() {
        AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
        aa.setDuration(1500);
        aa.setRepeatMode(Animation.REVERSE);
        aa.setRepeatCount(Animation.INFINITE);
        tvHind.startAnimation(aa);
    }

    private int time = 0;
    private Runnable runTime;

    private void runTimeShow(boolean isRun) {
        if (!isRun) {
            if (runTime != null) {
                handler.removeCallbacks(runTime);
                runTime = null;
            }
            return;
        }
        runTime = new Runnable() {
            @Override
            public void run() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        if (time < 10) {
                            tvTime.setText("0" + time);
                        } else {
                            tvTime.setText(time + "");
                        }
                        if (time >= maxLength) {
                            dismiss();
                            ToastUtils.showShort(context, "录音时间最长为60s");
                            return;
                        }
                    }
                });
                handler.postDelayed(runTime, 1000);
            }
        };
        handler.postDelayed(runTime, 1000);
    }

    private float alpha = 1.0f;

    public void onDismiss() {
        recordBundle.putInt(RECORD_TIME, time);
        recorderView.reset();
        RecorderUtils.getInstance().release();
        recorderView.setShowTime(0);
        time = 0;
        runLine(false);
        runTimeShow(false);

        alphaRun = new Runnable() {
            @Override
            public void run() {
                alpha += 0.1;
                if(alpha<=1){
                    handler.postDelayed(alphaRun,50);
                    WindowUtils.setBgAlpha((Activity) context,alpha);
                } else {
                    handler.removeCallbacks(alphaRun);
                }

            }
        };
        handler.postDelayed(alphaRun, 50);

    }

    private String recordName;

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }
}
