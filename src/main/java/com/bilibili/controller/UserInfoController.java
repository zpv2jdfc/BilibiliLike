package com.bilibili.controller;

import com.bilibili.common.utils.JWTUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("user")
public class UserInfoController {
    @PostMapping(value = "/info")
    public String getUserInfo(HttpSession session){
        return "for test";
    }
}
