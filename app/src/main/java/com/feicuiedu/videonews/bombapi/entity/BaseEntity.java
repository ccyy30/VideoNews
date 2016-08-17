package com.feicuiedu.videonews.bombapi.entity;

import java.util.Date;

/**
 * Bomb数据库中的通用字段
 */
public abstract class BaseEntity {

    // 唯一Id，由Bomb自动生成
    private String objectId;

    // 创建时间，由Bomb自动生成
    private Date createdAt;

    // 修改时间，由Bomb自动生成
    private Date updatedAt;

    public String getObjectId() {
        return objectId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
