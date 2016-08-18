package com.feicuiedu.videonews.bombapi.result;


import java.util.List;

/**
 * 所有视频新闻查询结果m
 */
public class QueryResult<T> {

    private List<T> results;

    public List<T> getResults() {
        return results;
    }

//     {
//        "results": [{...},{...}]
//    }
}
