package com.bilibili.controller;

import org.junit.Test;

public class UserInfoControllerTest extends BaseControllerTest{
    @Test
    public void getVideo() throws Exception {
        String responseString = post("/user/info");
        System.out.println(responseString);
    }
}
