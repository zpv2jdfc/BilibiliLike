package com.bilibili.controller;

import com.bilibili.common.utils.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {

    @PostMapping(value = "/recommend")
    public ReturnData getRecommend(@RequestHeader("Authorization") String token){
        ReturnData res = new ReturnData();
        List<Map> list = new ArrayList<>();
        Map<String,String> carsouel = new HashMap<>();
        carsouel.put("url","https://i0.hdslb.com/bfs/banner/d595ef5e47a5eac9c6e99c6ba28af5dcfbfdeb98.png@976w_550h_1c_!web-home-carousel-cover.avif");
        carsouel.put("caption","caption");
        carsouel.put("link","www.bilibili.com");
        list.add(carsouel);

        Map<String,Object> map = new HashMap<>();
        map.put("recommend_carsouels", list);
        map.put("recommend_grids", list);
        res.setData(map);
        return res;
    }
}
