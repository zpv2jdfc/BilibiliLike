package com.bilibili.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * b站个人空间视图
 */
@Data
public class UserInfoVo {
    private long id;
    private String name;
    private String signature;
    @JsonProperty(value = "avatar")
    private byte[] avatar;
}
