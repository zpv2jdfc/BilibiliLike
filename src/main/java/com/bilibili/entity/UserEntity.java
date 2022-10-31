package com.bilibili.entity;

import lombok.Data;

@Data
public class UserEntity {
    private long id;
    private String nickName;
    private String avatar;
    private String singature;
    private long level;
    private long privilege;
    private long status;
    private String setting;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp lmTime;
}
