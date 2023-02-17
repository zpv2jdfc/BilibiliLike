package com.bilibili.controller;


import com.bilibili.common.manager.Impl.RedisTokenManager;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.UserService;
import com.bilibili.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/profile")
public class UserInfoController {


    @Autowired
    private UserService userService;

    @GetMapping(value = "/info")
    public ReturnData getUserInfo(@RequestParam("id")long id){
        return userService.getUserInfoById(id);
    }

    @PostMapping(value="/updateInfo")
    public ReturnData updateUserInfo(@RequestBody UserInfoVo vo, @RequestHeader Map<String, String> header){
        vo.setId(Long.parseLong(header.get("id")));
        return this.userService.updateUserIndo(vo);
    }
    @PostMapping(value="/updateAvatar")
    public ReturnData updateUserAvatar(UserInfoVo vo, @RequestHeader Map<String, String> header){
        vo.setId(Long.parseLong(header.get("id")));
        return this.userService.updateAvatar(vo);
    }
}
