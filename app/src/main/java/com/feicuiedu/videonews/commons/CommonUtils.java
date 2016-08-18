package com.feicuiedu.videonews.commons;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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

    /** 用于编码带中文字符的URL地址*/
    public static String encodeUrl(String urlStr){
        try {
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(),url.getUserInfo(),url.getHost(),url.getPort(),url.getPath(),url.getQuery(),url.getRef());
            return uri.toASCIIString();
        } catch (MalformedURLException |URISyntaxException e) {
            return urlStr;
        }
    }
}
