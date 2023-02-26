package com.bilibili.interceptor;

import com.bilibili.common.constant.HttpStatus;
import com.bilibili.config.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class BaseInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisUtils redisUtils;

    private final String watchHistoryKey = "watchHistory";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        String ip = request.getRemoteAddr();
        int ipInt = 0;
        for(String temp : ip.split("\\.")){
            ipInt = ipInt*256 + Integer.parseInt(temp);
        }
        String[] URI = request.getRequestURI().split("/");
        String videoId = URI[URI.length-1];
        this.redisUtils.sAdd(watchHistoryKey, videoId+":"+ipInt);
    }
}
