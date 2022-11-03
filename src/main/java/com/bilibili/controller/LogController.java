package com.bilibili.controller;

import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.manager.Impl.RedisTokenManager;
import com.bilibili.common.manager.TokenManager;
import com.bilibili.common.utils.JWTUtil;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.LogService;
import com.bilibili.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class LogController {

    @Autowired
    private LogService logService;
    @Autowired
    private RedisTokenManager tokenManager;

    @PostMapping(value = "/register")
    public ReturnData regist(@RequestBody UserRegisterVo vo){
        return logService.createUser(vo.getName(), vo.getIdentitytype(), vo.getIdentifier(), vo.getCredential());
    }

    @PostMapping(value = "/login")
    public ReturnData login(@RequestBody UserRegisterVo vo, HttpServletRequest request){
        ReturnData ret = logService.createUser(vo.getName(), vo.getIdentitytype(), vo.getIdentifier(), vo.getCredential());
        Map map = ret.getData(new TypeReference<Map>(){});
        long id = Long.parseLong(map.get("id").toString());
        String token = tokenManager.createToken(id);
        tokenManager.add(id, token);
        request.setAttribute("token", token);
        return ret;
    }
}
