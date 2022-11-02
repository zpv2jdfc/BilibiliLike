package com.bilibili.entity;

import lombok.Data;

@Data
public class OAuthEntity {
    private long id;
    private long userId;
    //登录类型
    private String identityType;
//    登陆标识
    private String identifier;
//    密码凭证
    private String credential;
    private java.util.Date createTime;
    private java.util.Date lmTime;
}
