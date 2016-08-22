package com.feicuiedu.videonews.bombapi.other;

import com.feicuiedu.videonews.bombapi.BombConst;

/**
 * 收藏新闻 / 取消收藏的请求体
 */
public class LikesOperation {

    private RelationOperation likes;

    public LikesOperation(String userId, RelationOperation.Operation operation){
        Pointer pointer = new Pointer(BombConst.TABLE_USER,userId);
        likes = new RelationOperation(operation,pointer);
    }

//    "likes": {
//        "__op": "RemoveRelation",   // 代表此操作是删除一个Relation
//                "objects": [
//        {
//            "__type": "Pointer",
//                "className": "_User",  // 用户表名
//                "objectId": 用户Id
//        }
//        ]
}
