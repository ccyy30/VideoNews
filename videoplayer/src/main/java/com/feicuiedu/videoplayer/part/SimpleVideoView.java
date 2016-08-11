package com.feicuiedu.videoplayer.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feicuiedu.videoplayer.R;
import com.feicuiedu.videoplayer.full.VideoViewActivity;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
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

    private static final int PROGRESS_MAX = 1000;

    // 用来更新播放进度的handler
    private final Handler handler = new Handler(){
        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(isPlaying){
                //获取当前播放的进度
                long current = mediaPlayer.getCurrentPosition();
                //获取总进度
                long duration = mediaPlayer.getDuration();
                //获取进度条目标进度
                int progress = (int) (current * PROGRESS_MAX / duration);
                // 更新当前播放进度的进度条
                progressBar.setProgress(progress);
                // 每200毫秒，再更新一次
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

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
        // surfaceview的初始化
        initSurfaceView();
        // 控制视图的初始化
        initControllerView();
    }

    //预览图片
    private ImageView ivPreview;
    //播放/暂停
    private ImageButton ibToggle;
    //进度条
    private ProgressBar progressBar;

    //初始化控制视图
    private void initControllerView() {
        // 预览图片，默认是盖在surfaceview前面的
        // 预览图片
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        // 播放/暂停 按钮
        ibToggle = (ImageButton) findViewById(R.id.btnToggle);
        ibToggle.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                //播放和暂停
                if (mediaPlayer.isPlaying()) {
                    pauseMediaPlayer();
                } else if (isPrepared) {
                    startMediaPlayer();
                } else {
                    Toast.makeText(getContext(), "Can't play now !", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //要控制进度条，设置最大值
        progressBar.setMax(PROGRESS_MAX);
        // 全屏按钮
        ImageButton btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                //切换成全屏播放的Activity，使用VideoView播放
                VideoViewActivity.open(getContext(), videoPath);
            }
        });

    }

    //显示视频的控件
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    //初始化surfaceview
    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        //这里surface由于是在activity处于onResume状态时显示的，所以已经创建结束，所以就不需要再addCallback了
        surfaceHolder = surfaceView.getHolder();
        // 注意：Vitamio在使用SurfaceView播放时,要format
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
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

    //媒体播放器
    private MediaPlayer mediaPlayer;
    //记录播放器是否准备完毕
    private boolean isPrepared;

    // 初始化MediaPlayer, 设置一系列的监听
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
        mediaPlayer.setDisplay(surfaceHolder);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override public void onPrepared(MediaPlayer mp) {
                isPrepared = true;
                // 准备好后，自动开始播放
                startMediaPlayer();
            }
        });

        //视频播放时由于会画面宽高不适配产生失真效果，所以需要根据视频宽高比例调整surfaceview的宽高
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                // 根据宽的数据,去适配高的数据,宽度还是不动，调整高度
                int videoWidth = surfaceView.getWidth();
                int videoHeight = videoWidth * height/width;
                // 重置surfaceview宽高
                ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
                layoutParams.width = videoWidth;
                layoutParams.height = videoHeight;
                surfaceView.setLayoutParams(layoutParams);
            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override public boolean onInfo(MediaPlayer mp, int what, int extra) {
                //如果媒体播放文件已经打开成功，what是状态
                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK) {
                    // 注意：Vitamio5.0 要对音频进行设置才能播放
                    // 否则，不能播放在线视频
                    long bufferSize = mediaPlayer.audioTrackInit();
                    mediaPlayer.audioInitedOk(bufferSize);
                    return true;
                }
                return false;
            }
        });
    }

    //记录是否正在播放状态
    private boolean isPlaying;

    // 开始MediaPlayer, 同时更新UI状态
    private void startMediaPlayer(){
        if (isPrepared) {
            mediaPlayer.start();
        }
        isPlaying = true;
        //每次播放时都需要发送消息给handler控制进度条进度
        handler.sendEmptyMessage(0);
        // 播放和暂停按钮图像的更新
        ibToggle.setImageResource(R.drawable.ic_pause);
    }

    // 准备MediaPlayer, 同时更新UI状态
    private void prepareMediaPlayer() {
        try {
            mediaPlayer.reset();
            // 设置资源
            mediaPlayer.setDataSource(videoPath);
            mediaPlayer.setLooping(true);
            // 异步准备
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 暂停mediaPlayer, 同时更新UI状态
    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        //暂停播放需要发消息给handler移除进度条的控制
        handler.removeMessages(0);
        // 播放和暂停按钮图像的更新
        ibToggle.setImageResource(R.drawable.ic_play_arrow);
    }

    // 释放mediaPlayer, 同时更新UI状态
    private void releaseMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
        isPlaying = false;
        isPrepared = false;
    }
}