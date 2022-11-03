package com.bilibili.vo;

import lombok.Data;

@Data
public class UserLoginVo {
    private String identitytype;
    private String identifier;
    private String credential;

}
