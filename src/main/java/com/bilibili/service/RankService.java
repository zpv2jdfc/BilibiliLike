package com.bilibili.service;

import com.alibaba.fastjson.JSON;
import com.bilibili.config.RedisUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RankService {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    @Qualifier(value = "scheduleThreadPool")
    private ScheduledExecutorService scheduleThreadPool;

    private final String rankList = "rankList";
    private final String rankName = "rank:";
    private final String playNum = "playNum";
    private final String bvToId = "bvToId:";
    public RankService(){
//        scheduleThreadPool.schedule(new Runnable() {
//            @Override
//            public void run() {
//                refreshRankList();
//                refreshHottest();
//            }
//        },5, TimeUnit.SECONDS);
    }



    private int firstWatchScore = 1;
    private int appendWatchScore = 1;

    public void refreshHottest(){
        long time = System.currentTimeMillis();
        int h = (int)(time/(1000*60*60));
        String key = rankName+h;
        List<DefaultTypedTuple> temp = this.redisUtils.rangeWithScore(key);
        List list = new ArrayList<>();
        for(int i=0; i<10&&i<temp.size(); ++i){
            DefaultTypedTuple item = temp.get(i);
            Map map = new HashMap();
            map.put("score", item.getScore());
            map.put("id", Long.parseLong(item.getValue().toString()));
            list.add(map);
        }
        ObjectMapper mapper = new ObjectMapper();
        String listString = "";
        try {
            listString = mapper.writeValueAsString(list);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        this.redisUtils.set(rankList, listString);
    }
    public List getHottest(){
        ObjectMapper mapper = new ObjectMapper();
        List res=  null;
        try{
            res = mapper.readValue(this.redisUtils.get(rankList).toString(), List.class);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return res;
    }
    public boolean watchVideo(long videoId, long time){
//        播放量
        this.redisUtils.hIncr(playNum, String.valueOf(videoId));
//一小时热度
        int h = (int)(time/(1000*60*60));
        int m = (int)(time/(1000*60*10));
        long expireTime = time/(1000*60*10)*(1000*60*10) + 1000*60*60;
        String key = rankName+h+":"+m;
        if(redisUtils.hasKey(key)){
            redisUtils.zadd(key, videoId, appendWatchScore);
            redisUtils.expireAt(key, new Date(expireTime));
        }else {
            redisUtils.zadd(key, videoId, firstWatchScore);
            redisUtils.expireAt(key, new Date(expireTime));
        }
        return true;
    }

    public void refreshRankList(){
        long time = System.currentTimeMillis();
        int h = (int)(time/(1000*60*60));
        int m = (int)(time/(1000*60*10));
        long expireTime = time/(1000*60*10)*(1000*60*10) + 1000*60*60;
        String key = rankName+h+":"+m;
        String newKey = rankName+h;
        List<String> otherKeys = new ArrayList<>();
        for(int i=0;i<6;++i){
            String temp = rankName;
            m-=1;
            if(m==-1){
                m=0;
                h-=1;
            }
            otherKeys.add(temp+h+":"+m);
        }

        redisUtils.unionAndStore(key,otherKeys,newKey);
        redisUtils.expireAt(newKey, new Date(expireTime));
    }
}
