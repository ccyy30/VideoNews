package com.feicuiedu.videonews.ui.news;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.bombapi.BombClient;
import com.feicuiedu.videonews.bombapi.VideoApi;
import com.feicuiedu.videonews.bombapi.entity.VideoEntity;
import com.feicuiedu.videonews.bombapi.result.VideoResult;
import com.feicuiedu.videonews.commons.ToastUtils;
import com.mugen.Mugen;
import com.mugen.MugenCallbacks;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 带下拉刷新及上拉加载更多,及数据获取的视图
 */
public class NewsListView extends FrameLayout implements
        SwipeRefreshLayout.OnRefreshListener, MugenCallbacks{
    public NewsListView(Context context) {
        this(context, null);
    }

    public NewsListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @BindView(R.id.recyclerView) protected RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) protected SwipeRefreshLayout refreshLayout;
    @BindView(R.id.progressBar) protected ProgressBar progressBar;

    private VideoApi videoApi;

    private NewsListAdapter adapter;

    private boolean loadAll; // 是否已加载完所有数据(limit VS 服务器返回的数据量)

    private void initView() {
        videoApi = BombClient.getsInstance().getVideoApi();
        LayoutInflater.from(getContext()).inflate(R.layout.partial_pager_resource, this, true);
        ButterKnife.bind(this);
        // 初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsListAdapter();
        recyclerView.setAdapter(adapter);
        // 配置下拉刷新
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 配置上拉加载
        Mugen.with(recyclerView,this).start();
    }

    private final int limit = 5;
    private int skip = 0;
    // 下拉时将来触发的方法 (SwipeRefreshLayout)
    @Override public void onRefresh() {
        // 下拉刷新（获取最新数据）
        Call<VideoResult> call = videoApi.getVideoNewsList(limit, 0);
        // 返回null，一般说明查询条件未满足
        if (call == null) {
            refreshLayout.setRefreshing(false);// 停止下拉视图
            return;
        }
        call.enqueue(new Callback<VideoResult>() {
            @Override public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
                refreshLayout.setRefreshing(false);// 停止下拉视图
                // 取出响应数据(视频新闻列表)
                List<VideoEntity> datas =  response.body().getResults();
                skip = datas.size();
                loadAll = datas.size() < limit;
                // 将数据添加到Adapter进行视图刷新显示
                adapter.clear();
                adapter.addData(datas);
            }
            @Override public void onFailure(Call<VideoResult> call, Throwable t) {
                refreshLayout.setRefreshing(false);// 停止下拉视图
                ToastUtils.showShort("Failure:" + t.getMessage());
            }
        });
    }

    // 上拉时将来触发的方法 (Mugen + RecyclerView)
    @Override public void onLoadMore() {
        // 下拉刷新（获取最新数据）
        Call<VideoResult> call = videoApi.getVideoNewsList(limit, skip);
        // 返回null，一般说明查询条件未满足
        if (call == null) {
            ToastUtils.showShort("查询条件异常");
            return;
        }
        progressBar.setVisibility(View.VISIBLE); // 显示上拉视图
        call.enqueue(new Callback<VideoResult>() {
            @Override public void onResponse(Call<VideoResult> call, Response<VideoResult> response) {
                progressBar.setVisibility(View.GONE); // 隐藏上拉视图
                // 取出响应数据(视频新闻列表)
                List<VideoEntity> datas =  response.body().getResults();
                // 获取到的数据量不足limit，说明服务器没有更多数据了
                loadAll = datas.size() < limit;
                skip += datas.size();
                // 将数据添加到Adapter进行视图刷新显示
                adapter.addData(datas);
            }
            @Override public void onFailure(Call<VideoResult> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // 隐藏上拉视图
                ToastUtils.showShort("Failure:" + t.getMessage());
            }
        });
    }

    @Override public boolean isLoading() {
        return progressBar.getVisibility() == View.VISIBLE;
    }

    @Override public boolean hasLoadedAllItems() {
        return loadAll;
    }
}
