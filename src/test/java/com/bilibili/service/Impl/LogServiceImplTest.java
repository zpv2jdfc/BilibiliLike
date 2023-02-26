package com.bilibili.service.Impl;

import com.bilibili.dao.OAuthMapper;
import com.bilibili.dao.UserMapper;
import com.bilibili.service.LogService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static org.junit.Assert.*;

public class LogServiceImplTest extends BaseServiceTest {
    @Autowired
    private LogService logService;

    @Test
    @Rollback(value = false)
    public void createUser() {
        logService.createUser("sass","PASSWORD",null,"123456");
    }

    @Test
    public void sendMail(){
        logService.sendVerifyMail("793012545@qq.com");
    }

    @Test
    public void emailLog(){


    }
}
