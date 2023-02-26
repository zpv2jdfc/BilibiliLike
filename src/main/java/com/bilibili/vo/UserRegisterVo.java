package com.bilibili.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
@Data
public class UserRegisterVo {
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 5, max = 20, message = "用户名长度在5-20字符")
    private String name;

    private String identitytype;
    private String identifier;
    private String credential;
    private String verificationCode;
}
