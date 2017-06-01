package com.shen.snote.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.shen.snote.Const;
import com.shen.snote.MiApplication;
import com.shen.snote.utils.ToastUtils;

/**
 * Created by shen on 2017/3/8.
 */

public class SEditText extends EditText implements TextView.OnEditorActionListener {

    public static final int MODE_NULL = 0;
    public static final int MODE_SOLID_CIRCLE = 1;
    public static final int MODE_HOLLOW_CIRCLE = 2;
    public static final int MODE_SORT = 3;

    private static final String TXT_SOLID_CIRCLE = Const.txtModeSpaceTwo + "●" + Const.txtModeSpaceOne;
    private static final String TXT_HOLLOW_CIRCLE = Const.txtModeSpaceTwo + "○" + Const.txtModeSpaceOne;

    private int sortIndex = 0;//排序角标

    private int proColor = 0; //记录上一次的字体颜色

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    private int txtMode = MODE_NULL;

    public int getTxtMode() {
        return txtMode;
    }

    public void setTxtMode(int txtMode) {
        this.txtMode = txtMode;
    }

    public int getProColor() {
        return proColor;
    }

    public void setProColor(int proColor) {
        this.proColor = proColor;
    }

    public SEditText(Context context) {
        super(context);
        init();
    }

    public SEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setOnEditorActionListener(this);
    }

    /**
     * 根据mode实际添加效果
     */
    public void addEffect() {
        String proStr = getText().toString();
        int selectionStart = getSelectionStart();
        if (txtMode == MODE_SOLID_CIRCLE) {
            setText(TXT_SOLID_CIRCLE + proStr);
            setSelection(selectionStart + 4);

        } else if (txtMode == MODE_HOLLOW_CIRCLE) {
            setText(TXT_HOLLOW_CIRCLE + proStr);
            setSelection(selectionStart + 4);
        } else if (txtMode == MODE_SORT) {
            setText(Const.txtModeSpaceTwo+sortIndex+"."+Const.txtModeSpaceOne + proStr);
            int length = String.valueOf(sortIndex).length();
            setSelection(selectionStart + 4+length);

        }
    }

    /**
     * 根据mode实际删除效果
     */
    public void deleteEffect() {
        String proStr = getText().toString().substring(4);
        int selectionStart = getSelectionStart();
        if (txtMode == MODE_SOLID_CIRCLE) {
            setText(proStr);
            setSelection(selectionStart - 4);
        } else if (txtMode == MODE_HOLLOW_CIRCLE) {
            setText(proStr);
            setSelection(selectionStart - 4);
        } else if (txtMode == MODE_SORT) {
            int length = String.valueOf(sortIndex).length();
            setText(getText().toString().substring(length+4));
            setSelection(selectionStart - 4 - length);
        }
    }

    public interface OnEnterClickListener{
        /**
         * 输入enter键时的回调
         * */
        void clickEnter();

        /**
         * 点击editText左边的图片
         * */
        void clickLeftImg();
    }
    private OnEnterClickListener onEnterClickListener;
    public void setOnEnterClickListener(OnEnterClickListener onEnterClickListener){
        this.onEnterClickListener = onEnterClickListener;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        String s = text.toString();
        s.startsWith("");
        if(text != null){
            boolean startsHollow = text.toString().startsWith(TXT_HOLLOW_CIRCLE);
            boolean startsSolid = text.toString().startsWith(TXT_SOLID_CIRCLE);
            boolean startsWith = s.toString().startsWith(Const.txtModeSpaceTwo+sortIndex+"."+Const.txtModeSpaceOne);
            if(!startsHollow && !startsSolid && !startsWith){
                setTxtMode(MODE_NULL);
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(txtMode == MODE_NULL){
            return false;
        }
        if (actionId == EditorInfo.IME_ACTION_SEND
                || actionId == EditorInfo.IME_ACTION_DONE
                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                //处理事件
            if(onEnterClickListener != null){
                onEnterClickListener.clickEnter();
            }
        }
        return (event.getKeyCode()==KeyEvent.KEYCODE_ENTER);
//        return false;
    }

    /**
     * 根据页码生成排序前缀
     * */
    public String getSortStr(int i){
        return Const.txtModeSpaceTwo+i+Const.txtModeSpaceOne;
    }

    boolean down = false;
    boolean up = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    // et.getCompoundDrawables()得到一个长度为4的数组，分别表示左右上下四张图片
        Drawable drawable = this.getCompoundDrawables()[0];
        //如果右边没有图片，不再处理
        if (drawable == null)
            return super.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < drawable.getIntrinsicWidth() && event.getY()< drawable.getIntrinsicHeight()) {
                    down = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() < drawable.getIntrinsicWidth() && event.getY()< drawable.getIntrinsicHeight()) {
                    up = true;
                }
                break;
        }

        if(down && up){
            ToastUtils.showShort(MiApplication.getContext(), "点击了图片");
            if(onEnterClickListener != null){
                onEnterClickListener.clickLeftImg();
            }
            down = false;
            up = false;
            return true;
        }
        return super.onTouchEvent(event);
    }
}
