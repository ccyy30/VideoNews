package com.feicuiedu.videoplayer.full;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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

    public CustomMediaController(Context context) {
        super(context);
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
                mediaPlayerControl.seekTo(position += 10);
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
                mediaPlayerControl.seekTo(position -= 10);
                //下方的控制器经常消失，比较麻烦，让它处于显示状态
                CustomMediaController.this.show();
            }
        });

        //调整屏幕亮度（左边） 和 调整音量（右边）
        

    }

    //在自定义视图上有两个按钮需要控制，所以需要MediaPlayerControl，重写父类的函数来截获MediaPlayerControl
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mediaPlayerControl = player;
        super.setMediaPlayer(player);
    }
}
