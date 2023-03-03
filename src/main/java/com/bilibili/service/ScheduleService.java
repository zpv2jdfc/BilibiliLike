package com.bilibili.service;

import com.bilibili.config.RedisUtils;
import com.bilibili.controller.WebSocketServer;
import com.bilibili.dao.VideoMapper;
import com.bilibili.vo.BarrageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
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

    public static Queue<BarrageVo> barrageQue = new LinkedBlockingQueue<BarrageVo>();

    private final String playNum = "playNum";
    private final String tableBiu = "tableBiu:";
    private final String thumbNum = "thumbNum:";

    private final long overTime = 1000*60;

    @Scheduled(cron = "1-2 * * * * ? ")
    public void savePlayNum(){

        Map map = redisUtils.hGetAll(playNum);
        for(Map.Entry entry : (Set<Map.Entry>)map.entrySet()){
            videoMapper.updatePlayNum(Long.parseLong(entry.getKey().toString()),Integer.parseInt(entry.getValue().toString()));
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

    @Scheduled(cron = "0/10 * *  * * ?")
    public void saveBiu(){
        List<BarrageVo> l = new ArrayList<>();
        while(!barrageQue.isEmpty()){
            l.add(barrageQue.poll());
        }
        String tableName = "tb_video_biu0" ;
        if(l.size()>0)
            this.videoMapper.addBarrages(tableName, l);
    }

    /**
     * 同步点赞数量
     */
    @Scheduled(cron = "0/10 * *  * * ?")
    public void saveThumb(){
        Set<String> set = redisUtils.keys(thumbNum);
        for(String key : set){
            long videoId = Long.parseLong(key.split(":")[1]);
            long num = Long.parseLong(redisUtils.get(key).toString());
            videoMapper.updateThumb(videoId, num);
        }
    }

    @Scheduled(cron = "0/30 * *  * * ?")
    public void keepAlive(){
        for(Map.Entry entry : WebSocketServer.webSocketServerMAP.entrySet()){
            WebSocketServer server = ((WebSocketServer)entry.getValue());
            if(System.currentTimeMillis() - server.lastHeart > (overTime)){
//                超时
                server.disConnect();
            }
        }
    }
}
