package com.bilibili.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bilibili.common.manager.Impl.RedisTokenManager;
import com.bilibili.common.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserInfoController {

    @Autowired
    private RedisTokenManager tokenManager;

    @PostMapping(value = "/info")
    public String getUserInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        long id = tokenManager.getId(token);
        return "for test";
    }
}
