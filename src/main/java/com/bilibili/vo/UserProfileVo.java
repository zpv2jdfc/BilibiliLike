package com.bilibili.vo;

import lombok.Data;

/**
 * 评论区展示用户基本信息
 */
@Data
public class UserProfileVo {
    private long id;
    private String name;
    private String avatar;
    private String singature;
    private int level;
    private int privilege;
    private int status;
}
