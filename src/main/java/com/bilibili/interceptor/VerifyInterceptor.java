package com.bilibili.interceptor;

import com.bilibili.common.constant.HttpStatus;
import com.bilibili.common.manager.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class VerifyInterceptor implements HandlerInterceptor {
    @Autowired
    private TokenManager tokenManager;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("token");
        request.removeAttribute("userid");
        if(tokenManager.checkToken(token)){
            request.setAttribute("userid", tokenManager.getId(token));
        }
        return true;
    }
}
