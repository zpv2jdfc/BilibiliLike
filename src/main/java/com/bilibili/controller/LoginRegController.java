package com.bilibili.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginRegController {
    @RequestMapping("/login_success")
    public String loginSuccess() {
        return new String("");
    }

    /**
     *登陆方式
     * @param auth_type   1.QQ 2.微博 3.微信 4.手机号
     * @return
     */
    @GetMapping("/login/loginpage")
    public String userLogin(@RequestParam("auth_type") String auth_type){

        return null;
    }
}
