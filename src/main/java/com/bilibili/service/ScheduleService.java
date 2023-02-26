package com.bilibili.service;

import com.bilibili.config.RedisUtils;
import com.bilibili.dao.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@EnableScheduling
public class ScheduleService {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private RankService rankService;

    private final String playNum = "playNum";

    @Scheduled(cron = "1-2 * * * * ? ")
    public void savePlayNum(){

        Map map = redisUtils.hGetAll(playNum);
        for(Map.Entry entry : (Set<Map.Entry>)map.entrySet()){
            videoMapper.updatePlayNum(Long.parseLong(entry.getKey().toString()),(Integer) entry.getValue());
        }
    }
    @Scheduled(cron = "0/5 * *  * * ?")
    public void refershRank(){

        rankService.refreshRankList();
        rankService.refreshHottest();
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void clearWatchHhistory(){
        redisUtils.expire("watchHistory", 1, TimeUnit.SECONDS);
    }
}
