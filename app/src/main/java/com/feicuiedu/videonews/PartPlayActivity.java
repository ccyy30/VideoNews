package com.feicuiedu.videonews;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.feicuiedu.videoplayer.part.SimpleVideoView;
import butterknife.BindView;
import butterknife.ButterKnife;
//测试部分播放视频的界面
public class PartPlayActivity extends AppCompatActivity {

    //SimpleVideoView是部分播放视频控件
    @BindView(R.id.simpleVideoPlayer) SimpleVideoView simpleVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_play);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        //设置部分播放的视频路径
        simpleVideoView.setVideoPath(getTestVideo1());
    }

    //部分播放视频控件随着activity的生命周期调用开始播放和停止播放功能
    @Override protected void onResume() {
        super.onResume();
        simpleVideoView.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        simpleVideoView.onPasuse();
    }

    //获取视频路径
    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }
}
