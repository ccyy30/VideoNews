package com.feicuiedu.videoplayer.part;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.feicuiedu.videoplayer.R;

import io.vov.vitamio.Vitamio;

/**
 * 一个自定义的VideoView,使用MediaPlayer+SurfaceView来实现视频的播放
 * <p/>
 * MediaPlayer来做视频播放的控制，SurfaceView来显示视频
 * <p/>
 * 视图方面将简单实现:放一个播放/暂停按钮，一个进度条,一个全屏按钮,和一个SurfaceView
 * <p/>
 * 本API实现结构：
 * <ul>
 * <li/>提供setVideoPath方法(一定要在onResume方法调用前来调用): 设置播放谁
 * <li/>提供onResume方法(在activity的onResume来调用): 初始化MediaPlayer,准备MediaPlayer
 * <li/>提供onPause方法 (在activity的onPause来调用): 释放MediaPlayer,暂停mediaPlayer
 * </ul>
 * <p/>
 */
public class SimpleVideoView extends FrameLayout {

    public SimpleVideoView(Context context) {
        this(context, null);
    }

    public SimpleVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private String videoPath;

    private void init() {
        Vitamio.isInitialized(getContext());
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);
        initView();
    }

    // 对当前自定义视图做一些初始化工作
    private void initView() {

    }

    /** 设置播放谁(一定要在onResume方法调用前来调用): */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    /** 初始化MediaPlayer,准备MediaPlayer(和activity的onResume同步执行): */
    public void onResume(){
        initMediaPlayer();
        prepareMediaPlayer();
    }

    /** 暂停mediaPlayer,释放MediaPlayer(和activity的onPasuse同步执行): */
    public void onPasuse(){
        pauseMediaPlayer();
        releaseMediaPlayer();
    }

    // 初始化MediaPlayer, 设置一系列的监听
    private void initMediaPlayer() {

    }

    // 开始MediaPlayer, 同时更新UI状态
    private void startMediaPlayer(){

    }

    // 准备MediaPlayer, 同时更新UI状态
    private void prepareMediaPlayer() {

    }

    // 暂停mediaPlayer, 同时更新UI状态
    private void pauseMediaPlayer() {

    }

    // 释放mediaPlayer, 同时更新UI状态
    private void releaseMediaPlayer() {

    }
}