package com.bilibili.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.Assert.*;

public class VideoControllerTest extends BaseControllerTest{



    @Test
    public void getVideo() throws Exception {
        String responseString = post("/video/watch");
        System.out.println(responseString);
    }
}
