package com.bilibili.config;

import com.bilibili.interceptor.BaseInterceptor;
import com.bilibili.interceptor.LogInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    private LogInterceptor logInterceptor;
    @Autowired
    private BaseInterceptor baseInterceptor;
//   CORS
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**").allowedOrigins("http://localhost:4200");
//            }
//        };
//    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/user/**");
        registry.addInterceptor(baseInterceptor).addPathPatterns("/video/watch/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
