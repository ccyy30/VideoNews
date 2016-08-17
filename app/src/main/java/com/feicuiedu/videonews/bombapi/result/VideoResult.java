package com.feicuiedu.videonews.bombapi.result;


import com.feicuiedu.videonews.bombapi.entity.VideoEntity;

import java.util.List;

/**
 * 所有视频新闻查询结果
 */
public class VideoResult {

    private List<VideoEntity> results;

    public List<VideoEntity> getResults() {
        return results;
    }

    //{
//        "results": [{...},{...}]
//    }
}
