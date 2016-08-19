package com.feicuiedu.videonews.bombapi;

import com.feicuiedu.videonews.bombapi.entity.CommentsEntity;
import com.feicuiedu.videonews.bombapi.entity.NewsEntity;
import com.feicuiedu.videonews.bombapi.other.InQuery;
import com.feicuiedu.videonews.bombapi.result.QueryResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 视频新闻相关操作的Restful接口，包括获取新闻，收藏新闻，发表评论等
 */
public interface NewsApi {

    /**
     * 获取所有视频新闻列表, 接时间新到旧排序
     */
    @GET("1/classes/News?order=-createAt")
    Call<QueryResult<NewsEntity>> getVideoNewsList(@Query("limit") int limit, @Query("skip") int skip);

    /** 获取评论
     *  include参数是bomb服务器要求传递的需要获取详细信息的Pointer的字段描述
     *  author表示要获取用户数据
     *  InQuery where是请求服务器时要求传递的查询条件
     * */
    @GET("1/classes/Comments?include=author&order=-createdAt")
    Call<QueryResult<CommentsEntity>> getComments(
            @Query("limit") int limit,
            @Query("skip") int skip,
            @Query("where") InQuery where);
}
