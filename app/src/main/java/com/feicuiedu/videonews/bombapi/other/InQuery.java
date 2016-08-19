package com.feicuiedu.videonews.bombapi.other;

/**
 * 作者：yuanchao on 2016/8/18 0018 11:42
 * 邮箱：yuanchao@feicuiedu.com
 */
public class InQuery {

    private final String field;
    private final String className;
    private final String objectId;

    public InQuery(String field, String className, String objectId) {
        this.field = field;
        this.className = className;
        this.objectId = objectId;
    }

    // 查询当前表的news字段
    //    where = {
    //      "news": {
    //        "$inQuery": {
    //            "className": "News"
    //            "where": {
    //                "objectId": 新闻Id
    //            },
    //      }
//        }
//    }
    final String LIKES_IN_QUERY =
            "{ \"%s\": { \"$inQuery\": {\"where\": {\"objectId\":\"%s\"}, \"className\": \"%s\"}}}";

    @Override public String toString() {
        return String.format(LIKES_IN_QUERY, field, objectId, className);
    }
}