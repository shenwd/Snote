package com.shen.snote.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shen.snote.R;
import com.shen.snote.utils.ToastUtils;
import com.shen.snote.utils.WindowUtils;

/**
 * Created by shen on 2017/3/9.
 */

public class PopupWindowTxtSize extends PopupWindow implements RadioGroup.OnCheckedChangeListener {

    private Context context;

    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private RadioButton rb5;
    private RadioButton rb6;
    private RadioGroup rg;
    private RadioButton[] radios;
    private int[] colors;

    public PopupWindowTxtSize(Context context) {
        super(context);
        this.context = context;
        init();
        initData();
    }


    private void init() {

        // TODO: 2016/5/17 设置动画
       this.setAnimationStyle(R.style.popup_window_anim_bottom);
        // TODO: 2016/5/17 设置背景颜色
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        // TODO: 2016/5/17 设置可以获取焦点
        this.setFocusable(true);
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        this.setOutsideTouchable(true);
        // TODO：更新popupwindow的状态
        this.update();

        View view = LayoutInflater.from(context).inflate(R.layout.popup_txt_size,null);
        setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view);

        rg = (RadioGroup) view.findViewById(R.id.rg_color);
        rg.setOnCheckedChangeListener(this);

        rb1 = (RadioButton) view.findViewById(R.id.rb_color_1);
        rb2 = (RadioButton) view.findViewById(R.id.rb_color_2);
        rb3 = (RadioButton) view.findViewById(R.id.rb_color_3);
        rb4 = (RadioButton) view.findViewById(R.id.rb_color_4);
        rb5 = (RadioButton) view.findViewById(R.id.rb_color_5);
        rb6 = (RadioButton) view.findViewById(R.id.rb_color_6);

        radios = new RadioButton[]{
                rb1,rb2,rb3,rb4,rb5,rb6
        };

        int color1 = context.getResources().getColor(R.color.colorCircle1);
        int color2 = context.getResources().getColor(R.color.colorCircle2);
        int color3 = context.getResources().getColor(R.color.colorCircle3);
        int color4 = context.getResources().getColor(R.color.colorCircle4);
        int color5 = context.getResources().getColor(R.color.colorCircle5);
        int color6 = context.getResources().getColor(R.color.colorCircle6);

        colors = new int[]{
                color1,color2,color3,color4,color5,color6
        };

    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        WindowUtils.setBgAlpha((Activity) context,0.3f);
    }

    private void initData() {
        rb1.setChecked(true);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowUtils.setBgAlpha((Activity) context,1.0f);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_color_1:
                setCheckEffect(1);
                if(onItemClickListener != null)onItemClickListener.click(colors[0]);
                break;
            case R.id.rb_color_2:
                setCheckEffect(2);
                if(onItemClickListener != null)onItemClickListener.click(colors[1]);
                break;
            case R.id.rb_color_3:
                setCheckEffect(3);
                if(onItemClickListener != null)onItemClickListener.click(colors[2]);
                break;
            case R.id.rb_color_4:
                setCheckEffect(4);
                if(onItemClickListener != null)onItemClickListener.click(colors[3]);
                break;
            case R.id.rb_color_5:
                setCheckEffect(5);
                if(onItemClickListener != null)onItemClickListener.click(colors[4]);
                break;
            case R.id.rb_color_6:
                setCheckEffect(6);
                if(onItemClickListener != null)onItemClickListener.click(colors[5]);
                break;
            default:
                break;
        }
    }

    /**
     * 设置check后的界面效果
     * */
    private void setCheckEffect(int index) {
        for(int i = 0;i<radios.length;i++){
            if((i+1) == index){
                radios[i].setText("√");
            } else{
                radios[i].setText("");
            }
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void click(int color);
    }

    /**
     * item点击事件，从1开始
     * */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
