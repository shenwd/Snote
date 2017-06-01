package com.shen.snote.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.shen.snote.CompileActivity;
import com.shen.snote.R;
import com.shen.snote.utils.WindowUtils;

/**
 * Created by shen on 2017/3/9.
 */

public class PopupWindowTxtSort extends PopupWindow {

    private Context context;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    public PopupWindowTxtSort(Context context) {
        super(context);
        this.context = context;
        init();
        initData();
    }


    private void init() {

        View view = LayoutInflater.from(context).inflate(R.layout.popup_select_bottom,null);
        setWindowLayoutMode(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(view);
        // TODO: 2016/5/17 设置动画
       this.setAnimationStyle(R.style.popup_window_anim_left);
        // TODO: 2016/5/17 设置背景颜色
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F8F8F8")));
        // TODO: 2016/5/17 设置可以获取焦点
        this.setFocusable(true);
        // TODO: 2016/5/17 设置可以触摸弹出框以外的区域
        this.setOutsideTouchable(true);
        // TODO：更新popupwindow的状态
        this.update();

        tv1 = (TextView) view.findViewById(R.id.tv_1);
        tv2 = (TextView) view.findViewById(R.id.tv_2);
        tv3 = (TextView) view.findViewById(R.id.tv_3);
    }

    private void initData() {
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.click(1);
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.click(2);
            }
        });

        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.click(3);
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowUtils.setBgAlpha((Activity) context,1.0f);
            }
        });
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        WindowUtils.setBgAlpha((Activity) context,0.3f);
    }

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void click(int i);
    }

    /**
     * item点击事件，从1开始
     * */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
