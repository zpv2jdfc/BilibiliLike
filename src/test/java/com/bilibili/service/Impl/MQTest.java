package com.bilibili.service.Impl;

import com.bilibili.config.MQService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MQTest extends BaseServiceTest{
    @Autowired
    private MQService mqService;

    @Test
    public void testSend(){
        mqService.pushOneWayMessage("93h1hk5h35");
    }


}
