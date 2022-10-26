package com.bilibili.config;

import com.bilibili.interceptor.LogHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    private LogHandlerInterceptor logHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logHandlerInterceptor).addPathPatterns("/user/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
