package com.shen.snote;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.shen.snote.bean.EditTextBean;
import com.shen.snote.bean.IEditNote;
import com.shen.snote.bean.NoteBean;
import com.shen.snote.bean.NoteBeans;
import com.shen.snote.utils.DensityUtils;
import com.shen.snote.utils.LogUtils;
import com.shen.snote.utils.SPUtils;
import com.shen.snote.utils.SdCardUtils;
import com.shen.snote.utils.ToastUtils;
import com.shen.snote.view.PopupRecorder;
import com.shen.snote.view.PopupWindowTxtSize;
import com.shen.snote.view.PopupWindowTxtSort;
import com.shen.snote.view.SEditText;
import com.shen.snote.view.SRelativeLayout;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 笔记编辑页面 重写版
 */
public class EditNoteACtivity extends BaseMainActivity implements View.OnClickListener {

    private static final int TAKE_PHONO = 1;
    public static final int REQUEST_RECORD = 2;
    private static final java.lang.String RECORD_NAME = "name";
    private static final java.lang.String RECORD_REMARK = "remark";
    private static final java.lang.String RECORD_PATH = "recordPath";
    private static final java.lang.String TAG = "EditNoteActivity";

    private LinearLayout llContent;//主布局
    private ImageView ivImg;
    private ImageView ivImg1;
    private ImageView ivImg2;
    private ImageView ivImg4;
    private ImageView ivImg5;

    private int textEventFinishColor = Color.GRAY;//勾选了待办事项后的文本颜色
    private View currentFocusView = null;//当前焦点所在的view
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

    private NoteBeans noteBeans;//保存全部笔记数据
    private List<NoteBean> noteList;

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

        popupRecorder = new PopupRecorder(this);
        popupTxtSize = new PopupWindowTxtSize(this);

        initData();

    }

    @Override
    protected int setView() {
        return R.layout.activity_edit_note_activity;
    }

    private void initData() {

        if (true) {//新建笔记

            noteBeans = new NoteBeans();
            noteList = new ArrayList<NoteBean>();

            SEditText sEditText = createSEditText();
            sEditText.setHint("添加笔记");
            currentFocusView = sEditText;
            llContent.addView(currentFocusView);
        }
        if (false) {//从已有笔记打开
            Intent intent = getIntent();
            if (intent == null) {
                LogUtils.d(TAG, "传入的intent为空");
                return;
            }
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                LogUtils.d(TAG, "传入的bundle为空");
                return;
            }
            String note = bundle.getString("", "");
            if (TextUtils.isEmpty(note)) {
                LogUtils.d(TAG, "获取note文本为空");
                return;
            }
            noteBeans = new Gson().fromJson(note, NoteBeans.class);
            if (noteBeans == null) {
                LogUtils.d(TAG, "转换NoteBeans失败");
                return;
            }

            noteList = noteBeans.getNoteBeens();

        }
    }

    private SEditText createSEditText() {
        final SEditText et = new SEditText(this);
//        et.setBackgroundColor(Color.BLUE);
        et.setTextSize(Const.noteEditTextSize);
        et.setTextColor(getResources().getColor(R.color.colorNoteText));
        et.setLineSpacing(0, 1.2f);
        et.setTextScaleX(1.0f);
        et.setPadding(DensityUtils.dp2px(this, 5), (int) DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5), DensityUtils.dp2px(this, 5));
        et.setGravity(Gravity.CENTER_VERTICAL);
        et.setBackground(null);
//        根据字体高度设置SEditText的高度
//        int fontHeight = FontUtils.getFontHeight(et.getTextSize());
//        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fontHeight));
        et.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

//       新建Bean文件保存到list
        NoteBean noteBean = new NoteBean();
        noteBean.setNoteMode(Const.NOTE_TYPE_NORMAL);
        noteList.add(noteBean);


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
            public void clickLeftImg() {
               /* int currentTextColor = currentSEditText.getCurrentTextColor();
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
            }*/
            }

            @Override
            public void clickEnter() {
                /*String proStr = currentSEditText.getText().toString();
                int selectionStart = currentSEditText.getSelectionStart();
                currentSEditText.setText(proStr.substring(0, selectionStart));
                int sortIndex = currentSEditText.getSortIndex();
                SEditText sEditText = createSEditText(proStr.substring(selectionStart));
                sEditText.setTxtMode(currentSEditText.getTxtMode());
                if (sortIndex != 0) sEditText.setSortIndex(++sortIndex);
                sEditText.addEffect();
                addSEditTextView(sEditText, llContent.indexOfChild(currentSEditText) + 1);
                sEditText.requestFocus();*/

                if (!(currentFocusView instanceof SEditText)) {
                    LogUtils.d(TAG, "当前view强转失败");
                    return;
                }
                SEditText sEditText = (SEditText) currentFocusView;
                String text = sEditText.getText().toString();

                int selectionStart = sEditText.getSelectionStart();

                String proStr = text.substring(0, selectionStart);
                String latterStr = text.substring(selectionStart, text.length());

                int indexOfChild = llContent.indexOfChild(currentFocusView);
                NoteBean bean = noteList.get(indexOfChild);
                int noteMode = bean.getNoteMode();

                SEditText nexEeditText = createSEditText();
                currentFocusView = nexEeditText;

                switch (noteMode) {
                    case Const.NOTE_TYPE_NORMAL:
                        sEditText.setText(proStr);
                        nexEeditText.setText(latterStr);
                        llContent.addView(nexEeditText, indexOfChild + 1);
                        nexEeditText.requestFocus();
                        break;
                    case Const.NOTE_TYPE_SORT:
                        break;
                    case Const.NOTE_TYPE_POINT:
                        sEditText.setText(proStr);
                        nexEeditText.setText(Const.TXT_SOLID_CIRCLE + latterStr);
                        llContent.addView(nexEeditText, indexOfChild + 1);
                        nexEeditText.requestFocus();
                        NoteBean nextBean = noteList.get(llContent.indexOfChild(nexEeditText));
                        nextBean.setNoteMode(Const.NOTE_TYPE_POINT);
                        break;
                    default:
                        break;
                }
            }
        });

//        监听删除键
        et.setOnKeyListener(new View.OnKeyListener()

        {
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
        et.setOnFocusChangeListener(new View.OnFocusChangeListener()

        {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    currentFocusView = et;
                }
            }
        });

        et.addTextChangedListener(new

                                          TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {

                /*int indexOfChild = llContent.indexOfChild(currentSEditText);
                String name = allViewsName.get(indexOfChild);
                EditTextBean editTextBean = (EditTextBean) allDataMaps.get(name);
                editTextBean.setTxt(String.valueOf(s));

                SPUtils.put(CompileActivity.this, tmpDataSP, tmpTxt + tmpViewIndex, String.valueOf(s));*/


                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {

                                              }
                                          });
        return et;
    }

    /**
     * 保存笔记内容到内存 List
     */
    private void saveMemory(NoteBean noteBean) {

        switch (noteBean.getNoteMode()) {

            case Const.NOTE_TYPE_NORMAL:


                break;

            case Const.NOTE_TYPE_SORT:

                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_compile_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_1:

                SEditText editText = (SEditText) this.currentFocusView;
                String text = editText.getText().toString();

                NoteBean noteBean = noteList.get(llContent.indexOfChild(currentFocusView));
                if (noteBean.getNoteMode() == Const.NOTE_TYPE_NORMAL) {
                    noteBean.setNoteMode(Const.NOTE_TYPE_POINT);
                    editText.setText(Const.TXT_SOLID_CIRCLE + text);
                } else if (noteBean.getNoteMode() == Const.NOTE_TYPE_POINT) {
                    editText.setText(text.substring(5, text.length()));
                    noteBean.setNoteMode(Const.NOTE_TYPE_NORMAL);
                }
                editText.setSelection(editText.getText().length());
                break;
            case R.id.iv_2:
                ToastUtils.showShort(this, "待办事件");
                /*if (currentSEditText.getCompoundDrawables()[0] != null) {
                    currentSEditText.setCompoundDrawables(null, null, null, null);
                } else {
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_menu_3x);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
//                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), 20);
                    currentSEditText.setCompoundDrawables(drawable, null, null, null);
                }*/
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
//                SRelativeLayout recordLayout = createRecordLayout(recordBundle);

            }
        });
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
}