package com.bilibili.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名不能为空")
    @Length(min = 5, max = 20, message = "用户名长度在5-20字符")
    private String userName;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 6, max = 30, message = "密码长度必须在6-30之间")
    private String passWord;

    @NotEmpty(message = "验证码不能为空")
    private String code;
}
