package com.feicuiedu.videonews;

import android.app.Application;

import com.feicuiedu.videonews.commons.ToastUtils;

public class VideoNewsApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        ToastUtils.init(this);
    }
}
