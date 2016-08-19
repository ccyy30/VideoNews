package com.feicuiedu.videonews.bombapi.other;

import com.google.gson.annotations.SerializedName;

/**
 * 通用POINTER
 */
public class Pointer {
//    "__type": "Pointer",
//    "className": "_User",
//    "objectId": "D5vlAAAJ",

    @SerializedName("__type")
    private String type = "Pointer";

    private String className;

    private String objectId;

    public Pointer(String className, String objectId) {
        this.className = className;
        this.objectId = objectId;
    }
}
