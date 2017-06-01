package com.shen.snote;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.TimedMetaData;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shen.snote.bean.EditTextBean;
import com.shen.snote.bean.IEditNote;
import com.shen.snote.bean.ImgBean;
import com.shen.snote.bean.RecorderBean;
import com.shen.snote.db.DbExecUtils;
import com.shen.snote.utils.DensityUtils;
import com.shen.snote.utils.FileUtils;
import com.shen.snote.utils.LogUtils;
import com.shen.snote.utils.MediaPlayUtils;
import com.shen.snote.utils.RecorderUtils;
import com.shen.snote.utils.SPUtils;
import com.shen.snote.utils.SdCardUtils;
import com.shen.snote.utils.TimeUtils;
import com.shen.snote.utils.ToastUtils;
import com.shen.snote.view.DialogRecorder;
import com.shen.snote.view.PopupRecorder;
import com.shen.snote.view.PopupWindowTxtSize;
import com.shen.snote.view.PopupWindowTxtSort;
import com.shen.snote.view.SEditText;
import com.shen.snote.view.SRelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 笔记编辑页面逻辑类
 * <p>
 * 实时保存逻辑：
 * 1. 目前支持三种类型的笔记记录：文本、录音、相片
 * 2. 每次笔记编辑都实时修改sp（tmpDataSP）文件以保存状态，
 * 3. 以putString保存每一单个布局，key值以如下规则命名分类递增：
 * 图片：img_1... 。录音: record_1 ...。文本排序种类一：txtSort1_1、种类二、txtSort2_1、种类三：txtSort_3。
 * 待办文本事件: txtWait_1。
 * 4. 单独保存一个sp文件（tmpDataSortSP）,用来实时顺序保存布局name
 * 5. 启动和退出当前页面都进行检测更新sp文件到数据库中，并删除临时状态保存sp文件
 */
public class CompileActivity extends BaseMainActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

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
    private SEditText currentSEditText = null;//当前焦点所在的view
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

    private void initData() {

        allViewsName = new ArrayList<String>();
        allDataMaps = new HashMap<String, IEditNote>();
        allViewMaps = new HashMap<String, View>();

        handler = new Handler();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        Date date = new Date();
        String format = sDateFormat.format(date);
        if (bundle == null || bundle.isEmpty()) {//新编辑笔记页面
            addSEditTextView(createSEditText(""), 0);//添加一个默认的SEditText
            SPUtils.put(this, tmpDataSP, "frist_edit_time", format);
            SPUtils.put(this, tmpDataSP, "last_edit_time", format);
        } else {
            modifyTxt(bundle);
        }
        initPopupView();

    }

    /**
     * 根据数据还原笔记页面
     */
    private void modifyTxt(Bundle bundle) {
        noteId = bundle.getInt("noteId");
        SPUtils.put(this, tmpDataSP, "noteId", noteId);

        String sortSp = (String) SPUtils.get(this, tmpDataSP, tmpDataSortSP, "");
        if (TextUtils.isEmpty(sortSp)) {
            return;
        }

        String[] split = sortSp.split("\\|");
        for (int i = 0; i < split.length; i++) {
            String sortS = split[i];
            if (sortS.startsWith("txtSort")) {
                String value = (String) SPUtils.get(this, tmpDataSP, sortS, "");
                addSEditTextView(createSEditText(value), viewPosition);
            } else if (sortS.startsWith("record")) {

                String value = (String) SPUtils.get(this, tmpDataSP, sortS, "");

                MediaPlayUtils.getInstance().startPlay(value);
                int duration = MediaPlayUtils.getInstance().getDuration();
                Bundle recordBundle = new Bundle();

                recordBundle.putInt(PopupRecorder.RECORD_TIME, duration);
                recordBundle.putString(RECORD_PATH, value);

                createRecordLayout(recordBundle);
            } else if (sortS.startsWith("img")) {
                String value = (String) SPUtils.get(this, tmpDataSP, sortS, "");
                Bitmap bitmap = BitmapFactory.decodeFile(value);
                createImgView(bitmap);
            }
        }

    }

    private void initPopupView() {

        popupRecorder = new PopupRecorder(this);
        popupTxtType = new PopupWindowTxtSort(this);
        popupTxtSize = new PopupWindowTxtSize(this);

        popupTxtSize.setOnItemClickListener(new PopupWindowTxtSize.OnItemClickListener() {
            @Override
            public void click(int color) {
                for (View view : allViewMaps.values()) {
                    if (view instanceof EditText) {
                        ((EditText) view).setTextColor(color);
                    }
                }
            }
        });

        popupTxtType.setOnItemClickListener(new PopupWindowTxtSort.OnItemClickListener() {
            @Override
            public void click(int i) {
                int txtMode = currentSEditText.getTxtMode();
                switch (i) {
                    case 1:
                        if (txtMode == SEditText.MODE_NULL) {
                            currentSEditText.setTxtMode(SEditText.MODE_SOLID_CIRCLE);
                            currentSEditText.addEffect();
                        } else if (txtMode == SEditText.MODE_SOLID_CIRCLE) {
                            currentSEditText.deleteEffect();
                            currentSEditText.setTxtMode(SEditText.MODE_NULL);
                        } else {
                            currentSEditText.deleteEffect();
                            currentSEditText.setTxtMode(SEditText.MODE_SOLID_CIRCLE);
                            currentSEditText.addEffect();
                        }

                        break;
                    case 2:
                        if (txtMode == SEditText.MODE_NULL) {
                            currentSEditText.setTxtMode(SEditText.MODE_HOLLOW_CIRCLE);
                            currentSEditText.addEffect();
                        } else if (txtMode == SEditText.MODE_HOLLOW_CIRCLE) {
                            currentSEditText.deleteEffect();
                            currentSEditText.setTxtMode(SEditText.MODE_NULL);
                        } else {
                            currentSEditText.deleteEffect();
                            currentSEditText.setTxtMode(SEditText.MODE_HOLLOW_CIRCLE);
                            currentSEditText.addEffect();
                        }
                        break;
                    case 3:
                        if (txtMode == SEditText.MODE_NULL) {
                            currentSEditText.setTxtMode(SEditText.MODE_SORT);
                            currentSEditText.setSortIndex(1);
                            currentSEditText.addEffect();
                        } else if (txtMode == SEditText.MODE_SORT) {
                            currentSEditText.deleteEffect();
                            currentSEditText.setTxtMode(SEditText.MODE_NULL);
                        } else {
                            currentSEditText.deleteEffect();
                            currentSEditText.setTxtMode(SEditText.MODE_SORT);
                            currentSEditText.addEffect();
                        }
                        break;

                }
                popupTxtType.dismiss();
            }
        });

    }

    /**
     * 设置颜色选择的界面效果
     *
     * @param rb    装有所有radiobutton的数组
     * @param index 选中的index、
     */
    private void setCheckColorEffect(RadioButton[] rb, int index) {
        for (int i = 0; i < rb.length; i++) {
            if (index == i) {
                rb[index].setText("√");
            } else {
                rb[index].setText("");
            }
        }
    }

    //    录音文件保存的总文件夹
    private String recordsDir = SdCardUtils.getSDCardPath() + "/snote/record/";
    private int recordTime = 0;

    private SRelativeLayout createRecordLayout(Bundle bundle) {

        final SRelativeLayout ll = (SRelativeLayout) LayoutInflater.from(this).inflate(R.layout.item_record, null, false);
        final TextView tvPlayTime = (TextView) ll.findViewById(R.id.tv_play_time);
        TextView tvRemark = (TextView) ll.findViewById(R.id.tv_remark);
        final TextView tvPlay = (TextView) ll.findViewById(R.id.tv_play);

        final int duration = bundle.getInt(PopupRecorder.RECORD_TIME);
        final String recordName = bundle.getString(RECORD_NAME);
        final String recordPath = bundle.getString(RECORD_PATH);
        String recordRemark = bundle.getString(RECORD_REMARK);

        if (duration < 10) {
            tvPlayTime.setText("0" + duration);
        } else {
            tvPlayTime.setText(duration + "");
        }

        tvPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tvPlay.getText().toString().equals("结束")) {
                    MediaPlayUtils.getInstance().stop();
                    tvPlayTime.setText("00");
                    tvPlay.setText("播放");
                    recordTime = 0;
                    if (recordTimeRun != null) {
                        handler.removeCallbacks(recordTimeRun);
                    }
                    return;
                }
                if (!TextUtils.isEmpty(recordName)) {
                    MediaPlayUtils.getInstance().startPlay(recordsDir + recordName + "/" + recordName);
                } else if (!TextUtils.isEmpty(recordPath)) {
                    MediaPlayUtils.getInstance().startPlay(recordPath);
                }


                recordTimeRun = new Runnable() {
                    @Override
                    public void run() {
                        recordTime++;
                        if (recordTime < 10) {
                            tvPlayTime.setText("0" + recordTime);
                        } else {
                            tvPlayTime.setText(recordTime + "");
                        }
                        if (recordTime < duration) {
                            tvPlay.setText("结束");
                            handler.postDelayed(recordTimeRun, 1000);
                        } else {
                            tvPlay.setText("播放");
                            recordTime = 0;
                            handler.removeCallbacks(recordTimeRun);
                        }
                    }
                };
                handler.postDelayed(recordTimeRun, 1000);
            }
        });
        if (!TextUtils.isEmpty(recordName)) {
            ll.setRecordPath(recordsDir + recordName + "/" + recordName);
        } else if (!TextUtils.isEmpty(recordPath)) {
            ll.setRecordPath(recordPath);
        }

        tvRemark.setText(recordRemark);
        addImgOrRecordToLayout(ll, null, bundle);

        return ll;
    }

    /**
     * 保存名称到顺序sp
     */
    private void putSortSp(String str) {
        String sortSp = (String) SPUtils.get(this, tmpDataSP, tmpDataSortSP, "");

        if (sortSp == null || TextUtils.isEmpty(sortSp)) {//为空
            sortSp = sortSp + str;
        } else {//不为空
            if (sortSp.contains("|")) {//不只是一个view
                String[] strings = sortSp.split("\\|");
                String[] newStrs = new String[strings.length + 1];
                for (int i = 0; i < newStrs.length; i++) {
                    if (i == viewPosition) {
                        newStrs[i] = str;
                    } else if (i < viewPosition) {
                        newStrs[i] = strings[i];
                    } else {
                        newStrs[i] = strings[i - 1];
                    }
                }
                for (int i = 0; i < newStrs.length; i++) {
                    if (i == 0) {
                        sortSp = newStrs[i];
                    } else {
                        sortSp = sortSp + "|" + newStrs[i];
                    }
                }

            } else {//只有一个view
                if (viewPosition == 0) {//添加到第一个
                    sortSp = str + "|" + sortSp;
                } else {//添加到最后一个
                    sortSp = sortSp + "|" + str;
                }
            }
        }
        SPUtils.put(this, tmpDataSP, tmpDataSortSP, sortSp);
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat sDateFormat2 = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        Date date = new Date();
        final String formatDate = sDateFormat2.format(date);
        String date1 = sDateFormat.format(date);
        final String recordName = date1;
        popupRecorder.setRecordName(recordName);
        popupRecorder.showAtLocation(llContent, Gravity.CENTER, 0, 0);
        popupRecorder.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupRecorder.onDismiss();
                Bundle recordBundle = popupRecorder.getRecordBundle();
                recordBundle.putString(RECORD_NAME, recordName);
                recordBundle.putString(RECORD_REMARK, formatDate);
                SRelativeLayout recordLayout = createRecordLayout(recordBundle);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayUtils.getInstance().release();

        saveSpToDb();
    }

    /**
     * 退出页面时将sp保存的数据同步到数据库中
     */
    private void saveSpToDb() {
        Map<String, ?> all = SPUtils.getAll(this, tmpDataSP);
        Set<String> keys = all.keySet();
        StringBuilder sb = new StringBuilder();
        ContentValues cv = new ContentValues();

        for (String key : keys) {
            if (key.startsWith(tmpTxt)) {
                sb.append(all.get(key));
            }
        }
        cv.put("text", sb.toString());
        try {
            String result = FileUtils.readSpFile(this, tmpDataSP);
            cv.put("all_sp", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (noteId == 0) {
            String lastTime = (String) SPUtils.get(this, tmpDataSP, "last_edit_time", "");
            String fristTime = (String) SPUtils.get(this, tmpDataSP, "frist_edit_time", "");
            cv.put("last_edit_time", lastTime);
            cv.put("frist_edit_time", fristTime);
            DbExecUtils.getInstance().insert(cv);
        } else {
            String lastTime = (String) SPUtils.get(this, tmpDataSP, "last_edit_time", "");
            cv.put("last_edit_time", lastTime);
            DbExecUtils.getInstance().updata(noteId, cv);
        }
        SPUtils.clear(this, tmpDataSP);
    }

    private ImageView createImgView(Bitmap bitmap) {
        ImageView imgView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        DensityUtils.dp2px(this, 180));
        imgView.setLayoutParams(layoutParams);
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setImageBitmap(bitmap);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        addImgOrRecordToLayout(imgView, bitmap, null);
        return imgView;
    }


    private int viewPosition;

    /**
     * 添加img或record到主布局
     * img不需要bundle
     */
    private void addImgOrRecordToLayout(View view, Bitmap bitmap, Bundle bundle) {


        if (currentSEditText == null) {
            //回复笔记页面时使用
            llContent.addView(view);
            viewPosition = llContent.getChildCount();
            saveImgOrRecord(view, bitmap, bundle);
            return;
        }
        int position = llContent.indexOfChild(currentSEditText);
        if (currentSEditText.getSelectionStart() == 0) {
            //光标在首位
            if (position == 0) {
                //添加到布局中的第一个view
                llContent.addView(view, 0);
                viewPosition = position;
            } else {
                //获取当前cursor的SEditText的上一个view
                if ((llContent.getChildAt(position - 1)) instanceof SEditText) {
                    llContent.addView(view, position);
                } else {
                    addSEditTextView(createSEditText(""), position);
                    position = llContent.indexOfChild(currentSEditText);
                    viewPosition = position;
                    llContent.addView(view, position);
                }
            }
            saveImgOrRecord(view, bitmap, bundle);
        } else if (currentSEditText.getSelectionStart() == currentSEditText.getText().length()) {
            //cursor在文字的最后边
            int childCount = llContent.getChildCount();
            if (position == childCount - 1) {
                //当前view是父布局的最后一个view
                llContent.addView(view, position + 1);
                viewPosition = position + 1;
                saveImgOrRecord(view, bitmap, bundle);
                addSEditTextView(createSEditText(""), position + 2);
            } else {
                //不是最后一个
                View nextView = llContent.getChildAt(position + 1);
                if (nextView instanceof SEditText) {
                    llContent.addView(view, position + 1);
                    viewPosition = position + 1;
                    saveImgOrRecord(view, bitmap, bundle);
                } else {
                    llContent.addView(view, position + 1);
                    viewPosition = position + 1;
                    saveImgOrRecord(view, bitmap, bundle);
                    addSEditTextView(createSEditText(""), position + 2);
                }
            }
        } else {
            //cursor在文字的中间
            int selectionStart = currentSEditText.getSelectionStart();
            String str = currentSEditText.getText().toString();
            String proStr = str.substring(0, selectionStart);
            String lastStr = str.substring(selectionStart, str.length());

            currentSEditText.setText(proStr);
            int indexOfChild = llContent.indexOfChild(currentSEditText);
            String name = allViewsName.get(indexOfChild);
            SPUtils.put(this, tmpDataSP, name, proStr);
            currentSEditText.setSelection(selectionStart);
            llContent.addView(view, position + 1);
            viewPosition = position + 1;
            saveImgOrRecord(view, bitmap, bundle);
            addSEditTextView(createSEditText(lastStr), position + 2);
        }
    }

    private void saveImgOrRecord(View view, Bitmap bitmap, Bundle bundle) {
        if (view instanceof ImageView) {
            saveImg((ImageView) view, bitmap);
        } else if (view instanceof RelativeLayout) {
            saveRecord((RelativeLayout) view, bundle);
        }
    }

    private void saveEditData(EditText editText) {
        tmpViewIndex++;
        //保存sp
        SPUtils.put(this, tmpDataSP, tmpTxt + tmpViewIndex, editText.getText().toString());
        putSortSp(tmpTxt + tmpViewIndex);

        //保存到list
        allViewMaps.put(tmpTxt + tmpViewIndex, editText);
        putSortList(tmpTxt + tmpViewIndex);
        EditTextBean editTextBean = new EditTextBean(IEditNote.IEDITNOTE_EDITTEXT, EditTextBean.EDIT_NORMAL, "");
        allDataMaps.put(tmpTxt + tmpViewIndex, editTextBean);
    }

    private void saveImg(ImageView iv, Bitmap bitmap) {
        //保存sp
        tmpViewIndex++;
        File file = FileUtils.checkFile(tmpImg + tmpViewIndex, 2);
        FileUtils.saveImgToFile(bitmap, file);
        SPUtils.put(this, tmpDataSP, tmpImg + tmpViewIndex, file.getAbsolutePath());
        putSortSp(tmpImg + tmpViewIndex);

//        保存在list
        allViewMaps.put(tmpImg + tmpViewIndex, iv);
        putSortList(tmpImg + tmpViewIndex);
        ImgBean imgBean = new ImgBean(IEditNote.IEDITNOTE_IMG, file.getAbsolutePath());
        allDataMaps.put(tmpImg + tmpViewIndex, imgBean);
    }

    private void saveRecord(RelativeLayout rl, Bundle bundle) {
        int duration = 0;
        String recordName = "";
        String recordRemark;
        if (bundle != null) {
            duration = bundle.getInt(PopupRecorder.RECORD_TIME);
            recordName = bundle.getString(RECORD_NAME);
            recordRemark = bundle.getString(RECORD_REMARK);
        }

        //保存sp
        tmpViewIndex++;
        String path = SdCardUtils.getSDCardPath() + "/snote/record/" + recordName + "/" + recordName;
        SPUtils.put(this, tmpDataSP, tmpRecord + tmpViewIndex, path);
        putSortSp(tmpRecord + tmpViewIndex);

        //保存在list中
        allViewMaps.put(tmpRecord + tmpViewIndex, rl);
        RecorderBean recorderBean = new RecorderBean(IEditNote.IEDITNOTE_RECORDER);
        recorderBean.setDuration(duration);
        recorderBean.setPath(recordsDir + recordName + "/" + recordName);
        recorderBean.setMode(IEditNote.IEDITNOTE_RECORDER);
        allDataMaps.put(tmpRecord + tmpViewIndex, recorderBean);

        putSortList(tmpRecord + tmpViewIndex);
    }

    /**
     * 保存view名称到list
     */
    private void putSortList(String str) {
        if (viewPosition < allViewsName.size()) {
            allViewsName.add(viewPosition, str);
        }
        allViewsName.add(str);
        LogUtils.d("nameSort", allViewsName.toString());
    }

    /**
     * 将新的SEditText进行记录
     */
    private void addSEditTextView(SEditText sEditText, int position) {
        viewPosition = position;
        if (llContent.getChildCount() == 1 && llContent.getChildAt(1) instanceof EditText) {
            sEditText.setHint("输入便签");
        }

        // 添加到主布局中
        llContent.addView(sEditText, position);

        saveEditData(sEditText);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                break;
            case KeyEvent.KEYCODE_HOME:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 新建一个SEditText,并配置
     */
    private SEditText createSEditText(String str) {
        final SEditText et = new SEditText(this);
//        et.setBackgroundColor(Color.BLUE);
        et.setTextSize(DensityUtils.sp2px(this, Const.noteEditTextSize));
        et.setTextColor(getResources().getColor(R.color.colorNoteText));
        et.setLineSpacing(0, 1.2f);
        et.setTextScaleX(1.0f);
        et.setPadding(DensityUtils.dp2px(this, 5), (int) DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5));
        et.setGravity(Gravity.CENTER_VERTICAL);
        et.setBackground(null);
        et.setText(str);
//        根据字体高度设置SEditText的高度
//        int fontHeight = FontUtils.getFontHeight(et.getTextSize());
//        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fontHeight));
        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        try {
            //通过反射动态改变光标的样式，既改变mCursorDrawableRes的值
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(et, R.drawable.cursor_edittext_color);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

//        监听enter键和左边图片点击的监听
        et.setOnEnterClickListener(new SEditText.OnEnterClickListener() {
            @Override
            public void clickEnter() {
                String proStr = currentSEditText.getText().toString();
                int selectionStart = currentSEditText.getSelectionStart();
                currentSEditText.setText(proStr.substring(0, selectionStart));
                int sortIndex = currentSEditText.getSortIndex();
                SEditText sEditText = createSEditText(proStr.substring(selectionStart));
                sEditText.setTxtMode(currentSEditText.getTxtMode());
                if (sortIndex != 0) sEditText.setSortIndex(++sortIndex);
                sEditText.addEffect();
                addSEditTextView(sEditText, llContent.indexOfChild(currentSEditText) + 1);
                sEditText.requestFocus();

            }

            @Override
            public void clickLeftImg() {
                int currentTextColor = currentSEditText.getCurrentTextColor();
                if (currentTextColor == textEventFinishColor) {
                    currentSEditText.setTextColor(currentSEditText.getProColor());
                    //取消划线效果
                    currentSEditText.getPaint().setFlags(0);
                } else {
                    currentSEditText.setProColor(currentTextColor);
                    currentSEditText.setTextColor(textEventFinishColor);
                    //设置下划线
                    currentSEditText.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });

//        监听删除键
        et.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN
                        && TextUtils.isEmpty(et.getText().toString())) {
                    int indexOfChild = llContent.indexOfChild(et);
                    llContent.removeView(et);
                    for (int i = indexOfChild - 1; i >= 0; i--) {
                        if (llContent.getChildAt(i) instanceof EditText) {
                            EditText editText = (EditText) llContent.getChildAt(i);
                            editText.requestFocus();
                            int length = 0;
                            String s = editText.getText().toString();
                            if (s != null) {
                                length = s.length();
                            }
                            editText.setSelection(length);
                            break;
                        }
                    }
                    return true;
                }
                return false;
            }
        });

//        焦点转换
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentSEditText = et;
                    viewPosition = llContent.indexOfChild(currentSEditText);
                }
            }
        });

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                int indexOfChild = llContent.indexOfChild(currentSEditText);
                String name = allViewsName.get(indexOfChild);
                EditTextBean editTextBean = (EditTextBean) allDataMaps.get(name);
                editTextBean.setTxt(String.valueOf(s));

                SPUtils.put(CompileActivity.this, tmpDataSP, tmpTxt + tmpViewIndex, String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return et;
    }


    @Override
    protected int setView() {
        return R.layout.activity_compile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compile_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

        }
        return true;    //返回为true
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_1:
                popupTxtType.showAtLocation(findViewById(R.id.ll_global), Gravity.BOTTOM | Gravity.LEFT
                        , DensityUtils.dp2px(this, 20), DensityUtils.dp2px(this, 120));
                break;
            case R.id.iv_2:
                ToastUtils.showShort(this, "待办事件");
                if (currentSEditText.getCompoundDrawables()[0] != null) {
                    currentSEditText.setCompoundDrawables(null, null, null, null);
                } else {
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_menu_3x);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), 20);
                    currentSEditText.setCompoundDrawables(drawable, null, null, null);
                }
                break;
            case R.id.iv_3:
                ToastUtils.showShort(this, "添加相片");
                showSelectImgDialog();
                break;
            case R.id.iv_4:
                startRecord();
                break;
            case R.id.iv_5:
                popupTxtSize.showAtLocation(findViewById(R.id.ll_global), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL
                        , 0, 0);
                break;
            default:
                break;
        }
    }

    /**
     * 显示图片来源选择的dialog
     */
    private void showSelectImgDialog() {
        String[] itmes = {"拍照", "从相册里选择"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("添加相片");
        dialog.setIcon(R.drawable.ic_launcher);
        dialog.setItems(itmes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {//拍照
                    selectImgFromTakePhote();
                } else if (which == 1) {//从相册里面选择
                    selectImgFromPhoteAlbum();
                }

                dialog.dismiss();
            }
        });
        dialog.create().show();
    }

    /**
     * 打开相册
     */
    private void selectImgFromPhoteAlbum() {
        ToastUtils.showShort(this, "打开相册");
        // 激活系统图库，选择一张图片
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, TAKE_PHONO);

    }

    /**
     * 打开拍照
     */
    private void selectImgFromTakePhote() {
        ToastUtils.showShort(this, "打开拍照");
        if (SdCardUtils.isSDCardEnable()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, TAKE_PHONO);
        } else {
            ToastUtils.showShort(this, "内存卡不可用");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case TAKE_PHONO:
                if (data.getData() != null || data.getExtras() != null) {
                    {
                        Bitmap bitmap = null;
                        Uri uri = data.getData();
                        if (uri != null) {
                            String path = uri.getPath();
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (bitmap == null) {
                            Bundle bundle = data.getExtras();
                            if (bundle != null) {
                                bitmap = (Bitmap) bundle.get("data");
                                showPhoto(bitmap);
                            } else {
                                ToastUtils.showShort(this, "找不到照片");
                            }
                        } else {
                            showPhoto(bitmap);
                        }
                    }
                }
                break;

            case REQUEST_RECORD:
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    createRecordLayout(bundle);

                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示照片
     *
     * @param bitmap 图片
     */

    private void showPhoto(Bitmap bitmap) {
        View view = createImgView(bitmap);
    }
}
