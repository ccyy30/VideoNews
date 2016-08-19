package com.feicuiedu.videonews.ui.comments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.bombapi.BombClient;
import com.feicuiedu.videonews.bombapi.BombConst;
import com.feicuiedu.videonews.bombapi.NewsApi;
import com.feicuiedu.videonews.bombapi.entity.CommentsEntity;
import com.feicuiedu.videonews.bombapi.entity.NewsEntity;
import com.feicuiedu.videonews.bombapi.other.InQuery;
import com.feicuiedu.videonews.bombapi.result.QueryResult;
import com.feicuiedu.videonews.commons.CommonUtils;
import com.feicuiedu.videoplayer.part.SimpleVideoView;
import com.google.gson.Gson;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 新闻评论页面,主要包括三个部分:
 * <ul>
 * <li/>上: 用MediaPlayer +SurfaceView实现的视频播放, {@link SimpleVideoView}
 * <li/>下: 评论列表视图 {@link com.feicuiedu.videonews.ui.base.BaseResourceView}
 * <li/>选项菜单中有评论和收藏
 * </ul>
 */
public class CommentsActivity extends AppCompatActivity {

    private static final String KEY_NEWS = "KEY_NEWS";
    private NewsEntity newsEntity; // 视频新闻实体

    public static void open(Context context, NewsEntity newsEntity) {
        // NewsEntity --> json字符串
        Gson gson = new Gson();
        String news = gson.toJson(newsEntity);
        //
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(KEY_NEWS, news);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取传入当前页面的数据
        String news = getIntent().getStringExtra(KEY_NEWS);
        Gson gson = new Gson();
        newsEntity = gson.fromJson(news, NewsEntity.class);
        setContentView(R.layout.activity_comments);

        //以下测试获取新闻评论数据
        NewsApi newsApi = BombClient.getsInstance().getNewsApi();

        String newsId = newsEntity.getObjectId();
        InQuery where = new InQuery(BombConst.TABLE_NEWS,BombConst.FIELD_NEWS,newsId);
        Call<QueryResult<CommentsEntity>> call = newsApi.getComments(3,0,where);
        call.enqueue(new Callback<QueryResult<CommentsEntity>>() {
            @Override
            public void onResponse(Call<QueryResult<CommentsEntity>> call, Response<QueryResult<CommentsEntity>> response) {
                List<CommentsEntity> list = response.body().getResults();
                for(int x = 0; x < list.size(); x++){
                    Log.i("TAG",list.get(x).getContent()+","+list.get(x).getAuthor().getUsername()+","+
                    list.get(x).getObjectId());
                }
            }

            @Override
            public void onFailure(Call<QueryResult<CommentsEntity>> call, Throwable t) {
                Log.i("TAG","error");
            }
        });
    }

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tvTitle)
    TextView tvTitle; //
    @BindView(R.id.simpleVideoPlayer)
    SimpleVideoView simpleVideoView;

    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        // 设置toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //
        tvTitle.setText(newsEntity.getNewsTitle());
        // 设置播放源
        String videoPath = CommonUtils.encodeUrl(newsEntity.getVideoUrl());
        simpleVideoView.setVideoPath(videoPath);
    }

    @Override protected void onResume() {
        super.onResume();
        simpleVideoView.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        simpleVideoView.onPasuse();
    }
}
