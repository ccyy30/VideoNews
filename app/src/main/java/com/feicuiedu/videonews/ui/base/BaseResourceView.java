package com.feicuiedu.videonews.ui.base;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.feicuiedu.videonews.R;
import com.feicuiedu.videonews.bombapi.BombClient;
import com.feicuiedu.videonews.bombapi.NewsApi;
import com.feicuiedu.videonews.bombapi.result.QueryResult;
import com.feicuiedu.videonews.commons.ToastUtils;
import com.mugen.Mugen;
import com.mugen.MugenCallbacks;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 带下拉刷新及分页加载功能的自定义视图
 * <p>
 * 本API已完成列表视图上下拉获取数据 及 使用适配器适配显示数据的核心业务流程
 * <p>
 * 子类只需重写 queryData(),getLimit()和createItemView(),分别去获取不同数据及创建不同列表项视图即可
 * <p>
 * 列表视图使用 {@link RecyclerView}实现
 * <p>
 * 下拉刷新使用 {@link SwipeRefreshLayout}实现
 * <p>
 * 分页加载使用 {@link Mugen} + {@link ProgressBar} 实现
 * <p>
 * 数据获取使用 {@link NewsApi}实现
 * <p>
 */
public abstract class BaseResourceView<Model, ItemView extends BaseItemView<Model>> extends FrameLayout implements
        SwipeRefreshLayout.OnRefreshListener, MugenCallbacks {
    public BaseResourceView(Context context) {
        this(context, null);
    }

    public BaseResourceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseResourceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected NewsApi newsApi; // 数据获取接口(HTTP API)

    @BindView(R.id.recyclerView) protected RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) protected SwipeRefreshLayout refreshLayout;
    @BindView(R.id.progressBar) protected ProgressBar progressBar;

    private int skip = 0;
    private boolean loadAll; // 是否已加载完所有数据(limit VS 服务器返回的数据量)

    private ModelAdapter adapter; // 数据适配器

    protected void initView() {
        newsApi = BombClient.getsInstance().getNewsApi();
        LayoutInflater.from(getContext()).inflate(R.layout.partial_pager_resource, this, true);
        ButterKnife.bind(this);
        // 初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ModelAdapter();
        recyclerView.setAdapter(adapter);
        // 配置下拉刷新
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // 配置上拉加载
        Mugen.with(recyclerView, this).start();
    }

    public void autoRefresh() {
        refreshLayout.setRefreshing(true);
        onRefresh();
    }

    // 下拉时将来触发的方法 (SwipeRefreshLayout)
    @Override public void onRefresh() {
        // 获取数据(抽象)
        Call<QueryResult<Model>> call = queryData(getLimit(), 0);
        // 返回null，一般说明查询条件未满足
        if (call == null) {
            refreshLayout.setRefreshing(false);// 停止下拉视图
            return;
        }
        call.enqueue(new Callback<QueryResult<Model>>() {
            @Override public void onResponse(Call<QueryResult<Model>> call, Response<QueryResult<Model>> response) {
                refreshLayout.setRefreshing(false);// 停止下拉视图
                List<Model> datas = response.body().getResults();
                skip = datas.size();
                loadAll = datas.size() < getLimit();
                // 将"数据"添加到Adapter
                adapter.clear();
                adapter.addData(datas);
            }

            @Override public void onFailure(Call<QueryResult<Model>> call, Throwable t) {
                refreshLayout.setRefreshing(false);// 停止下拉视图
                ToastUtils.showShort("Failure:" + t.getMessage());
            }
        });
    }

    // 上拉时将来触发的方法 (Mugen + RecyclerView)
    @Override public void onLoadMore() {
        // 下拉刷新（获取最新数据）
        Call<QueryResult<Model>> call = queryData(getLimit(), skip);
        // 返回null，一般说明查询条件未满足
        if (call == null) {
            ToastUtils.showShort("查询条件异常");
            return;
        }
        progressBar.setVisibility(View.VISIBLE); // 显示上拉视图
        call.enqueue(new Callback<QueryResult<Model>>() {
            @Override public void onResponse(Call<QueryResult<Model>> call, Response<QueryResult<Model>> response) {
                progressBar.setVisibility(View.GONE); // 隐藏上拉视图
                // 取出响应数据(视频新闻列表)
                List<Model> datas = response.body().getResults();
                // 获取到的数据量不足limit，说明服务器没有更多数据了
                loadAll = datas.size() < getLimit();
                skip += datas.size();
                // 将数据添加到Adapter进行视图刷新显示
                adapter.addData(datas);
            }

            @Override public void onFailure(Call<QueryResult<Model>> call, Throwable t) {
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

    /**
     * 从服务器查询数据
     *
     * @param limit 要查询多少条
     * @param skip  要跳过多少条
     * @return {@link Call}
     */
    protected abstract Call<QueryResult<Model>> queryData(int limit, int skip);

    /**
     * 每页将从服务器获取多少条数据
     */
    protected abstract int getLimit();

    /**
     * 每个单项数据的视图
     */
    protected abstract ItemView createItemView();

    /**
     * RecyclerView的, 数据适配器
     */
    private class ModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final LinkedList<Model> dataSet = new LinkedList<>();

        public void clear() {
            dataSet.clear();
            notifyDataSetChanged();
        }

        public void addData(Collection<Model> data) {
            dataSet.addAll(data);
            notifyDataSetChanged();
        }

        @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemView itemView = createItemView();
            return new RecyclerView.ViewHolder(itemView) {
            };
        }

        @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // 当前项的数据
            Model model = dataSet.get(position);
            // 当前项的视图
            ItemView itemView = (ItemView) holder.itemView;
            // 将当前项的数据设置到当前项的视图上
            itemView.bindModel(model);
        }

        @Override public int getItemCount() {
            return dataSet.size();
        }
    }
}
