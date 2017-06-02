package com.shen.snote;

/**
 * Created by shen on 2017/3/5.
 */

public class Const {

    public static final int NOTE_TYPE_NORMAL = 1;
    public static final int NOTE_TYPE_SORT = 2;
    public static final int NOTE_TYPE_POINT = 3;
    public static final int NOTE_TYPE_img = 4;
    public static final int NOTE_TYPE_record = 5;

    //正文笔记字体默认大小
    public static int noteEditTextSize = 18;

    //空格
    public static String txtModeSpaceOne = "\t";
    public static String txtModeSpaceTwo = "\t\t";

    //
    public static final String TXT_SOLID_CIRCLE = Const.txtModeSpaceOne + "●" + Const.txtModeSpaceOne;

    /**
     * 用于正则表达式时
     * 需要转义的字符 . ? + $ ^ [ ] ( ) { } | \ / */
    public static String _splitSp = "!@#\\$";
    public static String _splitKey2Value = "key\\+:";

    /**
     * 与上面匹配，不使用在正则表达式中
     * */
    public static String splitSp = "!@#$";
    public static String splitKey2Value = "key+:";
}
