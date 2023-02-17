package com.bilibili.controller;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private VideoService videoService;

    @PostMapping(value = "/recommend")
    public ReturnData getRecommend(@RequestHeader("Authorization") String token){
        ReturnData res = new ReturnData();

//        滚动推荐
        List<Map> carsouels = new ArrayList<>();
        Map<String,String> carsouel = new HashMap<>();
        carsouel.put("url","https://i0.hdslb.com/bfs/banner/d595ef5e47a5eac9c6e99c6ba28af5dcfbfdeb98.png@976w_550h_1c_!web-home-carousel-cover.avif");
        carsouel.put("caption","caption");
        carsouel.put("link","www.bilibili.com");
        carsouels.add(carsouel);
//        6个推荐视频
        List grids = videoService.getRecommendVideo();

        Map<String,Object> map = new HashMap<>();
        map.put("recommend_carsouels", carsouels);
        map.put("recommend_grids", grids);
        res.setData(map);
        return res;
    }
}
