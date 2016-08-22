package com.feicuiedu.videonews.bombapi.other;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

/**
 * 多对多的关联关系
 */
public class RelationOperation {

    public enum Operation{
        AddRelation,
        RemoveRelation
    }

    @SerializedName("__op")
    private Operation operation;

    private List<Pointer> objects;

    public RelationOperation(Operation operation,Pointer... pointers){
        this.operation = operation;
        this.objects = Arrays.asList(pointers);
    }

//    "__op": "AddRelation",   // 代表此操作是添加一个Relation
//    "objects": [
//    {
//        "__type": "Pointer",
//            "className": "_User",  // 用户表名
//            "objectId": 用户Id
//    }
//    ]
}
