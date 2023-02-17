package com.bilibili.entity;

import lombok.Data;

@Data
public class UserEntity {
    private long id;
    private String nickName = "匿名用户";
    private String avatar = "default";
    private String signature = "";
//    用户等级
    private int level = 0;
//    会员等级 0普通用户 1会员用户 2高级会员， 默认0
    private int privilege = 0;
//    用户状态 0正常 1登陆异常暂时封禁 2发言不当暂停发言 3账号封禁，默认0
    private int status = 0;
//    用户个人空间显示设置
    private String setting;
    private java.util.Date createTime;
    private java.util.Date lmTime;
}
