package com.feicuiedu.videonews.ui.news;

import android.content.Context;
import android.util.AttributeSet;

import com.feicuiedu.videonews.bombapi.entity.NewsEntity;
import com.feicuiedu.videonews.bombapi.result.QueryResult;
import com.feicuiedu.videonews.ui.base.BaseResourceView;

import retrofit2.Call;

/**
 * 视频新闻列表视图，使用 {@link BaseResourceView}来完成
 */
public class NewsListView extends BaseResourceView<NewsEntity,NewsItemView>{
    public NewsListView(Context context) {
        super(context);
    }

    public NewsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected Call<QueryResult<NewsEntity>> queryData(int limit, int skip) {
        return newsApi.getVideoNewsList(limit,skip);
    }

    @Override protected int getLimit() {
        return 5;
    }

    @Override protected NewsItemView createItemView() {
        return new NewsItemView(getContext());
    }
}
