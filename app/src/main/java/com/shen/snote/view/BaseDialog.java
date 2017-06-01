package com.shen.snote.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by shen on 2017/3/14.
 */

public abstract class BaseDialog extends Dialog {
    private Context context;    //下面三个定义的跟上面讲得就是一样的啦
    private String title;
    private OnItemCheckListener onItemCheckListener;
    protected View view;    //看到这里我们定义的就清楚，我们也是借用view这个父类来引入布局的

    public BaseDialog(Context context) {
        super(context);
        this.context = context;
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
        if (onItemCheckListener != null)
            this.onItemCheckListener = onItemCheckListener;
    }


    protected void init() {
        //以view来引入布局
        View view = LayoutInflater.from(context).inflate(getLayoutId(), null);
        this.view = view;
        setContentView(view);
        //设置dialog大小
        Window dialogWindow = getWindow();
        WindowManager manager = ((Activity) context).getWindowManager();
        WindowManager.LayoutParams params = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        Display d = manager.getDefaultDisplay(); // 获取屏幕宽、高度
        params.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65，根据实际情况调整
        dialogWindow.setAttributes(params);
    }


    //可以看到这里定义了一个抽象方法，这个将交由子类去实现
    public abstract int getLayoutId();

    //为了逻辑分层管理，接口的管理实现方式
    public interface OnItemCheckListener {
        void onItemCheck(int checkedId);
    }
}
