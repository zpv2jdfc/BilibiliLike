package com.bilibili.service.Impl;

import com.bilibili.service.RankService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RankServiceTest extends BaseServiceTest{
    @Autowired
    private RankService rankService;

    @Test
    public void testRefresh(){
        rankService.watchVideo(2, System.currentTimeMillis());
        rankService.refreshRankList();
        rankService.refreshHottest();
        rankService.getHottest();
//        System.out.println(rankService.getHottest());
    }
}
