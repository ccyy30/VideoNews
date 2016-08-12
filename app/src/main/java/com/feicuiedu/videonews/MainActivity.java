package com.feicuiedu.videonews;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.feicuiedu.videonews.ui.local.LocalVideoFragment;
import com.feicuiedu.videoplayer.full.VideoViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// app
// videoplayer 视频播放
public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    @BindView(R.id.viewPager)ViewPager viewPager;
    @BindView(R.id.btnLikes)Button btnLikes;
    @BindView(R.id.btnLocal)Button btnLocal;
    @BindView(R.id.btnNews)Button btnNews;

    //主界面采用ViewPager+Fragment
    private final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new LocalVideoFragment();
                case 1:
                    return new LocalVideoFragment();
                case 2:
                    return new LocalVideoFragment();
                default:
                    throw new RuntimeException("不存在的数据");
            }
        }
        @Override public int getCount() {return 3;}
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        viewPager.setAdapter(adapter);
        // 对ViewPager进行监听(为了在Pager切换时，下方Button的切换)
        viewPager.addOnPageChangeListener(this);
        // 首次进入默认选中在线新闻
        btnNews.setSelected(true);
    }

    //点击下方三个按钮切换fragment
    @OnClick({R.id.btnLocal,R.id.btnNews,R.id.btnLikes})
    public void chooseFragment(View view){
        switch (view.getId()){
            case R.id.btnNews:
                viewPager.setCurrentItem(0, false);
                return;
            case R.id.btnLocal:
                viewPager.setCurrentItem(1, false);
                return;
            case R.id.btnLikes:
                viewPager.setCurrentItem(2, false);
        }
    }

    //获取测试视频路径
    private String getTestVideo1(){
        return "http://o9ve1mre2.bkt.clouddn.com/raw_%E6%B8%A9%E7%BD%91%E7%94%B7%E5%8D%95%E5%86%B3%E8%B5%9B.mp4";
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override public void onPageSelected(int position) {
        // ViewPager页面变化时设置下方按钮的选中状态
        btnNews.setSelected(position == 0);
        btnLocal.setSelected(position == 1);
        btnLikes.setSelected(position == 2);
    }

    @Override public void onPageScrollStateChanged(int state) {}
}
