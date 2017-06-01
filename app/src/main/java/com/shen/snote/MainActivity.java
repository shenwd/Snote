package com.shen.snote;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.shen.snote.db.DbExecUtils;
import com.shen.snote.utils.FileUtils;
import com.shen.snote.utils.SPUtils;
import com.shen.snote.utils.SdCardUtils;
import com.shen.snote.utils.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by shen on 2017/2/28.
 */

public class MainActivity extends BaseMainActivity implements Toolbar.OnMenuItemClickListener {


    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private List<NoteData> noteDatas;
    private AdapterMain adapterMain;

    @Override
    protected int setView() {
        return R.layout.drawer_layout_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        setSupportActionBar(toolbar);

        initData();
        initRecycleView();
        updataListFromDb();
    }

    /**
     * 更新
     */
    private void updataListFromDb() {
        noteDatas.clear();
        noteDatas.addAll(DbExecUtils.getInstance().queayAll());
        adapterMain.notifyDataSetChanged();
    }

    private void initData() {
        checkSp();

        toolbar.setOnMenuItemClickListener(this);
    }

    /**
     *
     * */
    private void checkSp() {
        String frist_edit_time = (String) SPUtils.get(this, CompileActivity.tmpDataSP, "frist_edit_time", "");
        if (frist_edit_time.equals("")) {
            //保存sp文件中的数据到数据库
        }
    }

    /**
     * 进入应用使检查sp文件是否存在，若存在更新数据到数据库中
     */
    private void saveSpToDb() {

        String tmpDataSP = "tmpDataSP";
        String tmpDataSortSP = "tmpDataSortSP";

        String tmpImg = "img_";//图片命名前缀
        String tmpRecord = "record_";//录音命名前缀
        String tmpTxt = "txtSort_";//普通文本命名前缀
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
            String allStr = FileUtils.readDateFile(this, "/com.shen.snote/shared_prefs/tmpDataSP.xml");
            cv.put("all_sp", allStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int noteId = (int) SPUtils.get(this, tmpDataSP, "noteId", 0);
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

    private void initRecycleView() {
        noteDatas = new ArrayList<NoteData>();

       /* for (int i = 0; i < 20; i++) {
            NoteData noteData = new NoteData(null, "asdfasdf:" + i, "", "", "", "");
            noteDatas.add(noteData);
        }*/

//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        adapterMain = new AdapterMain(getApplicationContext(), noteDatas);
        recyclerView.setAdapter(adapterMain);

        adapterMain.setOnItemClickListener(new AdapterMain.OnItemClickListener() {
            @Override
            public void click(int layoutPosition) {
                saveDbToSp(noteDatas.get(layoutPosition).getId());
                Intent intent = new Intent(MainActivity.this, CompileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("noteId",noteDatas.get(layoutPosition).getId());
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * 由创建过的笔记列表点击进入笔记详情页面之前，将对应的数据库笔记复制一份到sp文件，以便使用
     */
    private void saveDbToSp(int id) {

        Map<String, ?> all = SPUtils.getAll(this, CompileActivity.tmpDataSP);
        if (all != null && all.size() != 0) {
            saveSpToDb();
        }

        String all_sp = DbExecUtils.getInstance().query("all_sp", id);

        String[] split = all_sp.split(Const._splitSp);
        if (split == null || split.length == 0) {
            return;
        }

        for(int i = 0;i<split.length;i++){
            if(split[i].contains(Const.splitKey2Value)){
                String[] keyAndValue = split[i].split(Const._splitKey2Value);
                if(keyAndValue.length == 2){
                    SPUtils.put(this,CompileActivity.tmpDataSP,keyAndValue[0],keyAndValue[1]);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_add_main:
                Intent intent = new Intent(MainActivity.this, CompileActivity.class);
                MainActivity.this.startActivityForResult(intent, 0);
                break;

            case R.id.toolbar_r_img:
                Log.e("Test---->", "点击了右边图标");
                break;
            case R.id.toolbar_r_1:
                Log.e("Test---->", "点击了弹出菜单1");
                break;
            case R.id.toolbar_r_2:
                Log.e("Test---->", "点击了弹出菜单2");
                break;
            case R.id.toolbar_r_3:
                Log.e("Test---->", "点击了弹出菜单3");
                break;
        }
        return true;    //返回为true
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0://添加笔记返回时调用
                updataListFromDb();
                break;
        }
    }
}
