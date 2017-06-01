package com.shen.snote;

/**
 * Created by shen on 2017/3/5.
 */

public class Const {

    public static int noteEditTextSize = 14;
    public static String txtModeSpaceOne = "\t";
    public static String txtModeSpaceTwo = "\t\t";


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
