package com.bilibili.controller;

import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.UserService;
import com.bilibili.vo.UserLoginVo;
import com.bilibili.vo.UserRegisterVo;
import com.bilibili.vo.UserResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bilibili.common.constant.AuthConstant.LOGIN_USER;
import static com.bilibili.common.constant.BaseConstant.homePage;
import static com.bilibili.common.constant.BaseConstant.loginPage;
import static com.bilibili.common.constant.BaseConstant.regPage;

@RestController
@RequestMapping("auth")
public class LoginRegController {
    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vo, BindingResult result, RedirectAttributes attributes){
        if(result.hasErrors()){
            //注册错误返回注册页面
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors",errors);
            return regPage;
        }
        String code = vo.getCode();
        String redisCode = "";
        if(!(redisCode == null || redisCode.length() == 0)){
            if(code.equals(redisCode)){
                //删除验证码
                //调用注册
                ReturnData ret = userService.register(vo);
                if(ret.getCode() == 0){
                    //注册成功
                    return loginPage;
                }
                //注册失败
                Map<String,String> errors = new HashMap<>();
                errors.put("msg",ret.getData("msg",new TypeReference<String>(){}));
                attributes.addFlashAttribute("errors",errors);
                return regPage;
            }
            //验证码不对
            Map<String,String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            attributes.addFlashAttribute("errors",errors);
            return regPage;
        }
        //验证码失效
        Map<String,String> errors = new HashMap<>();
        errors.put("code","验证码失效,请重新输入");
        attributes.addFlashAttribute("errors",errors);
        return regPage;
    }
    @PostMapping(value = "/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session){
        ReturnData returnData = userService.login(vo);

        if(returnData.getCode() == 0){
            //身份验证成功
            UserResponseVo ret = returnData.getData(new TypeReference<UserResponseVo>(){});
            session.setAttribute(LOGIN_USER,ret);
            return homePage;
        }else {
            //验证失败
            Map<String,String> errors = new HashMap<>();
            errors.put("msg", returnData.getData("msg", new TypeReference<String>(){}));
            attributes.addFlashAttribute("errors",errors);
            return loginPage;
        }
    }
    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(LOGIN_USER);
        //未登录跳转到登陆页面
        if(attribute == null){
            return loginPage;
        }
        return homePage;
    }

    @GetMapping(value = "/logout.html")
    public String loginOut(HttpServletRequest request){
        request.getSession().removeAttribute(LOGIN_USER);
        request.getSession().invalidate();
        return homePage;
    }
}
