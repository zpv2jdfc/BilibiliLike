package com.bilibili.controller;

import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.manager.Impl.RedisTokenManager;
import com.bilibili.common.manager.TokenManager;
import com.bilibili.common.utils.JWTUtil;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.config.CacheService;
import com.bilibili.service.LogService;
import com.bilibili.vo.UserLoginVo;
import com.bilibili.vo.UserRegisterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LogController {

    @Autowired
    private LogService logService;
    @Autowired
    private RedisTokenManager tokenManager;
    @Autowired
    private CacheService cacheService;

    @PostMapping(value = "/register")
    public ReturnData register(@RequestBody UserRegisterVo vo, HttpServletResponse response){
        ReturnData ret = logService.createUser(vo.getName(), vo.getIdentitytype(), vo.getIdentifier(), vo.getCredential());
        //      注册信息验证出错
        if(Integer.parseInt(ret.get("code").toString()) != CodeEnum.SUCCESS.getCode()){
            return ret;
        }
//      成功注册，自动登录
        Map map = ret.getData(new TypeReference<Map>(){});
        long id = Long.parseLong(map.get("id").toString());
        String token = tokenManager.createToken(id);
        tokenManager.add(id, token);
        response.setHeader("token", token);
        return ret;
    }

    @PostMapping(value = "/login")
    public ReturnData login(@RequestBody UserLoginVo vo, HttpServletResponse response){
        ReturnData ret = logService.login(vo);
//      登陆验证出错
        if(Integer.parseInt(ret.get("code").toString()) != CodeEnum.SUCCESS.getCode()){
            return ret;
        }
//      成功登录，生成token
        Map map = ret.getData(new TypeReference<Map>(){});
        long id = Long.parseLong(map.get("id").toString());
        String token = tokenManager.createToken(id);
        tokenManager.add(id, token);
        response.setHeader("token", token);
        return ret;
    }
}
