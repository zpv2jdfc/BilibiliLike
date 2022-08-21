package com.bilibili.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class UserAuth {
    private Long id;
    private Long userId;
    private String identityType;
    private String identifier;
    private String credential;
    private Date createTime;
    private Date lastModifyTime;
}
