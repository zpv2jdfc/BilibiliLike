package com.bilibili.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bilibili.common.utils.JWTUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserInfoController {
    @PostMapping(value = "/info")
    public String getUserInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        DecodedJWT decode = JWT.decode(token);
        return "for test";
    }
}
