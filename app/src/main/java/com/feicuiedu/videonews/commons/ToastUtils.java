package com.feicuiedu.videonews.commons;


import android.content.Context;
import android.support.annotation.UiThread;
import android.widget.Toast;

/**
 * 显示Toast的工具类，所有方法都必须在UI线程调用。
 */
@UiThread
public class ToastUtils {

    private static Toast toast;

    private static Context context;

    public static void init(Context context){
        ToastUtils.context = context.getApplicationContext();
    }

    public static void showShort(String msg){
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }

        toast.show();
    }

    public static void showShort(int resId){
        showShort(context.getResources().getString(resId));
    }

}
