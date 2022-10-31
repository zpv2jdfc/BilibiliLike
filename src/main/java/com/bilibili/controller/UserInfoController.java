package com.bilibili.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bilibili.common.manager.Impl.RedisTokenManager;
import com.bilibili.common.utils.JWTUtil;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.UserService;
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
    @Autowired
    private UserService userService;

    @PostMapping(value = "/info")
    public ReturnData getUserInfo(HttpServletRequest request){
        long id = Long.parseLong(request.getHeader("userid"));
        return userService.getUserInfoById(id);
    }
}
