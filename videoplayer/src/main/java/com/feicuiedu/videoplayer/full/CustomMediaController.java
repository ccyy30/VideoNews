package com.feicuiedu.videoplayer.full;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.feicuiedu.videoplayer.R;

import io.vov.vitamio.widget.MediaController;

/**
 * 这是一个自定义的视频播放控制器
 *
 * 继承{@link MediaController}，实现自定义的视频播放控制器。
 * <p>
 * 重写{@link #makeControllerView()}方法，提供自定义的视图，视图规则如下：
 * <ul>
 * <li/>SeekBar的id必须是mediacontroller_seekbar
 * <li/>播放/暂停按钮的id必须是mediacontroller_play_pause
 * <li/>当前时间的id必须是mediacontroller_time_current
 * <li/>总时间的id必须是mediacontroller_time_total
 * <li/>视频名称的id必须是mediacontroller_file_name
 * <li/>drawable资源中必须有pause_button和play_button
 * </ul>
 * <p>
 * Created by Administrator on 2016/8/10 0010.
 */
public class CustomMediaController extends MediaController {

    private MediaPlayerControl mediaPlayerControl;
    private AudioManager audioManager;
    private Window window;
    //当前音量
    private int currentVolume;
    //当前亮度
    private float currentBrightness;
    //最大音量
    private int maxVolume;

    public CustomMediaController(Context context) {
        super(context);
        //初始化控制器
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        window = ((Activity)context).getWindow();
    }

    //这里需要使用Vitamio的视图资源id，然后自己准备新的视图替换Vitamio默认的视图
    @Override
    protected View makeControllerView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_video_controller,this,true);
        //设置自定义视图中快进和快退以及屏幕亮度和音量等功能
        initView(view);
        return view;
    }

    //设置自定义视图中快进和快退以及屏幕亮度和音量等功能
    private void initView(View view) {
        ImageButton ibFastRewind = (ImageButton) view.findViewById(R.id.btnFastRewind);
        //快退
        ibFastRewind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //当前播放的进度
                long position = mediaPlayerControl.getCurrentPosition();
                //在当前进度基础上增加10秒
                mediaPlayerControl.seekTo(position -= 10000);
                CustomMediaController.this.show();
            }
        });

        ImageButton ibFastForward = (ImageButton) view.findViewById(R.id.btnFastForward);
        //快进
        ibFastForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //当前播放的进度
                long position = mediaPlayerControl.getCurrentPosition();
                //在当前进度基础上减少10秒
                mediaPlayerControl.seekTo(position += 10000);
                //下方的控制器经常消失，比较麻烦，让它处于显示状态
                CustomMediaController.this.show();
            }
        });

        //调整屏幕亮度（左边） 和 调整音量（右边）
        //获取要手指滑动的视图
        final View adjustView = view.findViewById(R.id.adjustView);
        //使用安卓的GestureDectector 手势收集
        final GestureDetector gestureDetector = new GestureDetector(getContext(),new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                super.onScroll(e1, e2, distanceX, distanceY);
                //先获取手指触摸的起始位置和结束位置
                float startX = e1.getX();
                float startY = e1.getY();
                float endX = e2.getX();
                float endY = e2.getY();
                //获取整个视图的高度和宽度
                float width = adjustView.getWidth();
                float height = adjustView.getHeight();
                //获取手指移动Y轴在视图高度上的百分比
                float point = distanceY / height;
                //判断手指滑动是在屏幕左边还是右边
                if(startX < width / 3){
                    //调整亮度
                    adjustLight(point);
                    return true;
                }
                if(startX > width * 2 / 3){
                    //调整音量
                    adjustMusic(point);
                    return true;
                }
                return true;
            }
        });

        adjustView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //在手指按下时就先获取当前音量和亮度
                // 使用ACTION_MASK是为了过滤掉多点触屏事件
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    currentBrightness = window.getAttributes().screenBrightness;
                }
                //使用手势类来帮我们处理手势
                gestureDetector.onTouchEvent(event);
                //下方的控制器经常消失，比较麻烦，让它处于显示状态
                CustomMediaController.this.show();
                return true;
            }
        });

    }

    //根据百分比调整音量
    private void adjustMusic(float point) {
        //要设置的目标音量 = 当前音量 + 调整音量
        int targetVolume = (int) (point * maxVolume) + currentVolume;
        //判断音量是否已经到达最大或者最小
        targetVolume = targetVolume > maxVolume ? maxVolume : targetVolume;
        targetVolume = targetVolume < 0 ? 0 : targetVolume;
        //设置目标音量
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, targetVolume, AudioManager.FLAG_SHOW_UI);
    }

    //根据百分比调整亮度
    private void adjustLight(float point) {
        // 计算出目标亮度 = 调整的亮度 + 当前亮度，亮度是0-1的浮点
        float targetBrightness = point + currentBrightness;
        targetBrightness = targetBrightness > 1.0f ? 1.0f : targetBrightness;
        targetBrightness = targetBrightness < 0.01f ? 0.01f : targetBrightness;
        // 设置亮度
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = targetBrightness;
        window.setAttributes(layoutParams);
    }

    //在自定义视图上有两个按钮需要控制，所以需要MediaPlayerControl，重写父类的函数来截获MediaPlayerControl
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mediaPlayerControl = player;
        super.setMediaPlayer(player);
    }
}
