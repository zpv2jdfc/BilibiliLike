package com.bilibili.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
public class BaseConstant {

    @Value("${base.param.homePage}")
    public static String homePage ;

    @Value("${base.param.loginPage}")
    public static String loginPage ;

    @Value("${base.param.regPage}")
    public static String regPage ;

}
