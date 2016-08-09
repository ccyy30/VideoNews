package com.feicuiedu.videonews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.feicuiedu.videoplayer.full.VideoViewActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

// app
// videoplayer 视频播放
// vitamio
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnLocal)
    public void demoPlay(){
        // 开启VideoViewActivity进行视频播放
        VideoViewActivity.open(this, getTestVideo1());
    }

    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }
}
