package com.shen.snote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.shen.snote.CompileActivity;
import com.shen.snote.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Map;

/**
 * Created by shen on 2017/3/3.
 */

public class FileUtils {

    private FileUtils() {
    }

    public static void saveImgToFile(Bitmap bitmap,File file){
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查必要文件夹，并创建需要的文件夹以及文件
     * type = 1 录音，type = 2 相片
     */
    public static File checkFile(String name, int type) {

        File recordItemFile;//录音临时子文件，待合并的
        File recordDir;//保存所有录音文件的文件夹
        File recordItemDir;//以录音文件为名的文件夹,保存单一录音文件

        if (name != null && !TextUtils.isEmpty(name)) {
            if (SdCardUtils.isSDCardEnable()) {
                String sdCardPath = SdCardUtils.getSDCardPath();
                File snoteFile = new File(sdCardPath, "snote");
                if (!snoteFile.exists()) {
                    snoteFile.mkdirs();
                }

                if(type == 1){
                    recordDir = new File(snoteFile, "record");
                } else {
                    recordDir = new File(snoteFile, "img");
                }

                if (!recordDir.exists()) {
                    recordDir.mkdirs();
                }

                recordItemDir = new File(recordDir, name);
                if (!recordItemDir.exists()) {
                    recordItemDir.mkdirs();
                }

                recordItemFile = new File(recordItemDir, name);
                if (recordItemFile.exists()) {
                    recordItemFile.delete();
                }
                try {
                    recordItemFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return recordItemFile;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    // 读取sd中的文件
    public static String readSDCardFile(Context context,String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        String result = streamRead(context,fis);
        return result;
    }

    // 在res目录下建立一个raw资源文件夹，这里的文件只能读不能写入。。。
    public static String readRawFile(Context context,int fileId) throws IOException {
        // 取得输入流
        InputStream is = context.getResources().openRawResource(fileId);
        String result = streamRead(context,is);// 返回一个字符串
        return result;
    }

    private static String streamRead(Context context,InputStream is) throws IOException {
        int buffersize = is.available();// 取得输入流的字节长度
        byte buffer[] = new byte[buffersize];
        is.read(buffer);// 将数据读入数组
        is.close();// 读取完毕后要关闭流。
//        String result = EncodingUtils.getString(buffer, "UTF-8");// 设置取得的数据编码，防止乱码

        String result = String.valueOf(buffer);
        return result;
    }

    // 在assets文件夹下的文件，同样是只能读取不能写入
    public static String readAssetsFile(Context context,String filename) throws IOException {
        // 取得输入流
        InputStream is = context.getResources().getAssets().open(filename);
        String result = streamRead(context,is);// 返回一个字符串
        return result;
    }

    // 往sd卡中写入文件
    public static void writeSDCardFile(Context context,String path, byte[] buffer) throws IOException {
        File file = new File(path);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buffer);// 写入buffer数组。如果想写入一些简单的字符，可以将String.getBytes()再写入文件;
        fos.close();
    }

    // 将文件写入应用的data/data的files目录下
    public static void writeDateFile(Context context,String fileName, byte[] buffer) throws Exception {
        byte[] buf = fileName.getBytes("iso8859-1");
        fileName = new String(buf, "utf-8");
        // Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
        // Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
        // Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
        // MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
        // 如果希望文件被其他应用读和写，可以传入：
        // openFileOutput("output.txt", Context.MODE_WORLD_READABLE +
        // Context.MODE_WORLD_WRITEABLE);
        FileOutputStream fos = context.openFileOutput(fileName,
                Context.MODE_APPEND);// 添加在文件后面
        fos.write(buffer);
        fos.close();
    }

    // 读取应用的data/data的files目录下文件数据
    public static String readDateFile(Context context,String fileName) throws Exception {
        FileInputStream fis = context.openFileInput(fileName);
        String result = streamRead(context,fis);// 返回一个字符串
        return result;
    }

    /**
     * 需求:将两个amr格式音频文件合并为1个
     * 注意:amr格式的头文件为6个字节的长度
     * @param partsPaths      各部分路径
     * @param unitedFilePath  合并后路径
     */
    public static void uniteAMRFile(String[] partsPaths, String unitedFilePath) {
        try {
            File unitedFile = new File(unitedFilePath);
            FileOutputStream fos = new FileOutputStream(unitedFile);
            RandomAccessFile ra = null;
            for (int i = 0; i < partsPaths.length; i++) {
                ra = new RandomAccessFile(partsPaths[i], "r");
                if (i != 0) {
                    ra.seek(6);
                }
                byte[] buffer = new byte[1024 * 8];
                int len = 0;
                while ((len = ra.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
            }
            ra.close();
            fos.close();
        } catch (Exception e) {
        }
    }

    public static void deleteAllFile(String[] fileItemsPaths) {
        for(int i = 0;i<fileItemsPaths.length;i++){
            File file = new File(fileItemsPaths[i]);
            if(file.exists()){
                file.delete();
            }
        }
    }

    public static String readSpFile(Context context, String tmpDataSP) {

        Map<String, ?> all = SPUtils.getAll(context, tmpDataSP);

        StringBuilder sb = new StringBuilder();
        for(String key : all.keySet()){
            String value = "";
            if(TextUtils.equals(key,"noteId")){
               value  = String.valueOf( SPUtils.get(context,tmpDataSP,key,0));
            } else {
                value = String.valueOf(SPUtils.get(context,tmpDataSP,key,""));
            }

            if(sb.length() == 0){
                sb.append(key);
                sb.append(Const.splitKey2Value);
                sb.append(value);
            } else {
                sb.append(Const.splitSp);
                sb.append(key);
                sb.append(Const.splitKey2Value);
                sb.append(value);
            }
        }
        return sb.toString();
    }
}
