package com.bilibili.controller;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;

public class LoginRegControllerTest extends BaseControllerTest {

    @Test
    public void register() {
    }

    @Test
    public void login() {
    }

    @Test
    public void loginPage() {
    }

    @Test
    public void loginOut() throws Exception {
        String responseString = get("/aa/logout.html");
        System.out.println(responseString);
    }
}
