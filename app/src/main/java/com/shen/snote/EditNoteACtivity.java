package com.shen.snote;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shen.snote.bean.IEditNote;
import com.shen.snote.utils.SPUtils;
import com.shen.snote.view.PopupRecorder;
import com.shen.snote.view.PopupWindowTxtSize;
import com.shen.snote.view.PopupWindowTxtSort;
import com.shen.snote.view.SEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* 笔记编辑页面 重写版
* */
public class EditNoteACtivity extends BaseMainActivity implements View.OnClickListener {

    private static final int TAKE_PHONO = 1;
    public static final int REQUEST_RECORD = 2;
    private static final java.lang.String RECORD_NAME = "name";
    private static final java.lang.String RECORD_REMARK = "remark";
    private static final java.lang.String RECORD_PATH = "recordPath";

    private LinearLayout llContent;//主布局
    private ImageView ivImg;
    private ImageView ivImg1;
    private ImageView ivImg2;
    private ImageView ivImg4;
    private ImageView ivImg5;

    private int textEventFinishColor = Color.GRAY;//勾选了待办事项后的文本颜色
    private View currentFocusView = null;//当前焦点所在的view
    private PopupWindowTxtSort popupTxtType;//popup文本排序弹出窗
    private PopupWindowTxtSize popupTxtSize;//popup字体编辑弹出窗
    private Handler handler;
    private Runnable timeRun;
    private PopupRecorder popupRecorder;
    private Runnable recordTimeRun;


    private List<String> allViewsName;//保存所有的布局view的名字
    private Map<String, IEditNote> allDataMaps;//保存所有的布局数据
    private Map<String, View> allViewMaps;//保存所有的布局

    //    ----------------------------------------------------------------------
//    实时数据保存相关
//    用作 各种添加布局的key值的后缀，递增
    private int tmpViewIndex = 0;
    public static String tmpDataSP = "tmpDataSP";
    public static String tmpDataSortSP = "tmpDataSortSP";

    private String tmpImg = "img_";//图片命名前缀
    private String tmpRecord = "record_";//录音命名前缀
    private String tmpTxt = "txtSort_";//普通文本命名前缀
    private String tmpTxt1 = "txtSort1_";//文本排序种类一命名前缀
    private String tmpTxt2 = "txtSort2_";//文本排序种类二命名前缀
    private String tmpTxt3 = "txtSort3_";//文本排序种类三命名前缀
    private int noteId = 0;//笔记在数据库中的id

    //-------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        if (popupRecorder.isShowing()) {
            popupRecorder.dismiss();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        llContent = (LinearLayout) findViewById(R.id.ll_content);

        ivImg = (ImageView) findViewById(R.id.iv_3);
        ivImg1 = (ImageView) findViewById(R.id.iv_1);
        ivImg2 = (ImageView) findViewById(R.id.iv_2);
        ivImg4 = (ImageView) findViewById(R.id.iv_4);
        ivImg5 = (ImageView) findViewById(R.id.iv_5);
        ivImg.setOnClickListener(this);
        ivImg1.setOnClickListener(this);
        ivImg2.setOnClickListener(this);
        ivImg4.setOnClickListener(this);
        ivImg5.setOnClickListener(this);

        initData();

    }

    @Override
    protected int setView() {
        return R.layout.activity_edit_note_activity;
    }

    private void initData() {

        if(true){//新建笔记
            SEditText sEditText = createSEditText();
            sEditText.setHint("添加笔记");
            currentFocusView =sEditText;
            addSEditTextView(sEditText,Const.NOTE_TYPE_NORMAL);//默认添加第一个EditText
        }//从已有笔记打开



    }

    private SEditText createSEditText() {
        SEditText sEditText = new SEditText(this);

        return sEditText;
    }

    /**
     *
     * 向主布局添加Sedittext
     * @param sEditText 需要添加的view
     * @param noteTypeNormal 文本类型
     * */
    private void addSEditTextView(SEditText sEditText, int noteTypeNormal) {

    }




    @Override
    public void onClick(View v) {

    }
}
