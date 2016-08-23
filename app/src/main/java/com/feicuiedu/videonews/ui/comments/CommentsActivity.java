package com.feicuiedu.videonews.ui.comments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.bombapi.BombClient;
import com.feicuiedu.videonews.bombapi.NewsApi;
import com.feicuiedu.videonews.bombapi.entity.NewsEntity;
import com.feicuiedu.videonews.bombapi.other.LikesOperation;
import com.feicuiedu.videonews.bombapi.other.RelationOperation;
import com.feicuiedu.videonews.bombapi.result.UpdateResult;
import com.feicuiedu.videonews.commons.CommonUtils;
import com.feicuiedu.videonews.commons.ToastUtils;
import com.feicuiedu.videonews.ui.UserManager;
import com.feicuiedu.videoplayer.part.SimpleVideoView;
import com.google.gson.Gson;

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
public class CommentsActivity extends AppCompatActivity implements EditCommentFragment.OnCommentSuccessListener{

    private static final String KEY_NEWS = "KEY_NEWS";

    public static void open(Context context, NewsEntity newsEntity) {
        // NewsEntity --> json字符串
        Gson gson = new Gson();
        String news = gson.toJson(newsEntity);
        //
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(KEY_NEWS, news);
        context.startActivity(intent);
    }

    private NewsApi newsApi;
    private NewsEntity newsEntity; // 视频新闻实体
    private EditCommentFragment editCommentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsApi = BombClient.getsInstance().getNewsApi();
        // 获取传入当前页面的数据
        String news = getIntent().getStringExtra(KEY_NEWS);
        Gson gson = new Gson();
        newsEntity = gson.fromJson(news, NewsEntity.class);
        Log.i("DEBUG",news);
        setContentView(R.layout.activity_comments);
    }

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvTitle) TextView tvTitle; //
    @BindView(R.id.simpleVideoPlayer) SimpleVideoView simpleVideoView;
    @BindView(R.id.commentsListView) CommentsListView commentsListView;


    @Override public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        // 设置toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(newsEntity.getNewsTitle());

        // 告诉评论列表视图，针对哪一条新闻去获取评论数据
        commentsListView.setNewsId(newsEntity.getObjectId());
        commentsListView.autoRefresh();

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

    // -------------------------------
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_comments, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        // 判断是否"离线"
        if (UserManager.getInstance().isOffline()) {
            ToastUtils.showShort(R.string.please_login_first);
            return true;
        }
        // 收藏
        if (item.getItemId() == R.id.menu_item_like) {
            String userId = UserManager.getInstance().getObjectId();
            String newsId = newsEntity.getObjectId();
            LikesOperation likesOperation = new LikesOperation(userId, RelationOperation.Operation.AddRelation);
            Call<UpdateResult> likesCall = newsApi.changLikes(newsId, likesOperation);
            likesCall.enqueue(likesCallback);
        }
        // 评论
        if (item.getItemId() == R.id.menu_item_comment) {
            if (editCommentFragment == null) {
                String newsId = newsEntity.getObjectId();
                editCommentFragment = EditCommentFragment.getInstance(newsId);
                editCommentFragment.setListener(this);
            }
            editCommentFragment.show(getSupportFragmentManager(),"Edit Comment");
        }
        return super.onOptionsItemSelected(item);
    }

    private Callback<UpdateResult> likesCallback = new Callback<UpdateResult>() {
        @Override public void onResponse(Call<UpdateResult> call, Response<UpdateResult> response) {
            if (response.isSuccessful()) {
                ToastUtils.showShort(R.string.like_success);
                return;
            }
            ToastUtils.showShort(R.string.like_failure);
        }

        @Override public void onFailure(Call<UpdateResult> call, Throwable t) {
            ToastUtils.showShort(t.getMessage());
        }
    };

    @Override public void onCommentSuccess() {
        editCommentFragment.dismiss();
        // 刷新视图，获取最新评论
        commentsListView.autoRefresh();
    }
}
