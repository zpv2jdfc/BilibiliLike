package com.bilibili.controller;

import com.bilibili.BilibiliApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {BilibiliApplication.class})
public class BaseControllerTest {
    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        //此种方式可通过spring上下文来自动配置一个或多个controller
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //此种方式，手工指定想要的controller
        //mockMvc = MockMvcBuilders.standaloneSetup(new Controller1(), new Controller2()).build();
    }
    String post(String url) throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.post(url) //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON_UTF8) //数据的格式
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        return responseString;
    }
    String get(String url) throws Exception {
        String responseString = mockMvc.perform(MockMvcRequestBuilders.get(url) //请求的url,请求的方法是get
                        .contentType(MediaType.APPLICATION_JSON_UTF8) //数据的格式
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                ).andExpect(MockMvcResultMatchers.status().isOk())  //返回的状态是200
                .andDo(MockMvcResultHandlers.print()) //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString(); //将相应的数据转换为字符
        return responseString;
    }

}
