package com.shen.snote.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by shen on 2017/3/13.
 */

public class SRelativeLayout extends RelativeLayout {
    public SRelativeLayout(Context context) {
        super(context);
        init();
    }

    public SRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    /**
     * 相对应布局的录音路径
     * */
    private String recordPath;

    public String getRecordPath() {
        return recordPath;
    }

    public void setRecordPath(String recordPath) {
        this.recordPath = recordPath;
    }
}
