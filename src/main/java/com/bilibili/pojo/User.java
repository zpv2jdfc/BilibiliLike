package com.bilibili.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
public class User{
    private transient Long id;
    private String nickName;
    private String avatar;
    private String signature;
    private int level;
    private int status;
    private String setting;
    private transient Date createTime;
    private transient Date lastModifyTime;
    private transient UserAuth userAuth;
}
