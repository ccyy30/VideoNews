package com.feicuiedu.videonews.commons;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonUtils {

    private CommonUtils(){}

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    /**
     * 将Date对象转换成UI显示时使用的统一格式。
     */
    public static String format(Date date){
        return format.format(date);
    }

}
