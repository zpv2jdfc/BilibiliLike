package com.bilibili.service.Impl;

import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.BloomFilterHelper;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.config.MQService;
import com.bilibili.config.RedisUtils;
import com.bilibili.dao.VideoMapper;
import com.bilibili.service.RankService;
import com.bilibili.service.ScheduleService;
import com.bilibili.service.VideoService;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.UploadVideoVo;
import com.bilibili.vo.UserProfileVo;
import com.bilibili.vo.VideoVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    private RankService rankService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private BloomFilterHelper bloomFilterHelper;
    @Autowired
    private MQService mqService;

    private final String allBVCode = "allBVCode";

    private static AtomicInteger bvcode = new AtomicInteger(867);
    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String playNum = "playNum";
    private final String bvToId = "bvToId:";
    private final String thumbNum = "thumbNum:";
    private final String thumbed = "thumbed:";
    private final String cacheTag = "redisMessage";
    private final String tableBiu = "tableBiu:";

    @Override
    public List<Map> getBiu(long bvcode, int begin, int end) {
        long id = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            id = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            if(redisUtils.hasKey(allBVCode) && !this.redisUtils.includeByBloomFilter(bloomFilterHelper,allBVCode,String.valueOf(bvcode))){
                return null;
            }
            id = this.videoMapper.getIdByBVCode(bvcode);
        }
//        从缓存中取弹幕
        if(redisUtils.hasKey(tableBiu+id)){
            String jsonStr = redisUtils.get(tableBiu+id).toString();
            ObjectMapper mapper = new ObjectMapper();
            List res = new ArrayList();
            try {
                res = mapper.readValue(jsonStr, List.class);
            }catch (Exception e){
                log.info(e.getMessage());
            }
            return res;
        }
//          加锁，防止缓存击穿
        synchronized ((tableBiu+id).intern()){
            if(redisUtils.hasKey(tableBiu+id)){
                String jsonStr = redisUtils.get(tableBiu+id).toString();
                ObjectMapper mapper = new ObjectMapper();
                List res = new ArrayList();
                try {
                    res = mapper.readValue(jsonStr, List.class);
                }catch (Exception e){
                    log.info(e.getMessage());
                }
                return res;
            }
            String tableName = "tb_video_biu" + id/10000;
            if(this.redisUtils.hasKey(tableName+":"+id)){
                List<Map> l = (List)redisUtils.get(tableName+":"+id);
                redisUtils.expire(tableName+":"+id, 600l, TimeUnit.SECONDS);
//                return l;
            }
            List res = videoMapper.getBiu(tableName,id,begin,end);
            this.redisUtils.set(tableName+":"+id,res,600l);
            String jsonStr = null;
            try {
                jsonStr = new ObjectMapper().writeValueAsString(res);
            }catch (Exception e){
                log.info(e.getMessage());
            }
            redisUtils.set(tableBiu+id, jsonStr, 600l);
            return res;
        }
    }

    @Override
    public int addBiu(BarrageVo vo) {
        long bvcode=vo.getVideoId();
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            videoId = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            videoId = this.videoMapper.getIdByBVCode(bvcode);
        }
        synchronized ((tableBiu+videoId).intern()){
            if(redisUtils.hasKey(tableBiu+videoId)){
            String content = ",{\\\"biu_time\\\":"+vo.getTime()+",\\\"content\\\":\\\""+vo.getContent()+"\\\"}]\"";
            long len = redisUtils.stringSTRLEN(tableBiu+videoId);
            redisUtils.setStringRange(tableBiu+videoId, content, len-2);
            }else {
                ObjectMapper mapper = new ObjectMapper();
                String jsonStr = "";
                try{
                    List temp = new ArrayList();
                    temp.add(vo);
                    jsonStr = mapper.writeValueAsString(temp);
                }catch (Exception e){
                    log.info(e.getMessage());
                }
                redisUtils.setString(tableBiu+videoId, jsonStr);
            }
        }
        vo.setVideoId(videoId);
        ScheduleService.barrageQue.offer(vo);
        return 1;
//        String tableName = "tb_video_biu" + videoId/10000;
//        String content = vo.getContent();
//        long userId = 0;
//        int time = vo.getTime();
//        return videoMapper.addBiu(tableName,videoId,content,userId,time);
    }


    @Override
    public int addComment(long bvcode, String content, Date commentTime, long userId) {
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            videoId = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            videoId = this.videoMapper.getIdByBVCode(bvcode);
        }
        String tableName = "tb_video_comment" + videoId/10000;
        long parent = -1;

        int res = videoMapper.addComment(tableName, videoId, userId, content, parent, -1, new Timestamp(commentTime.getTime()),"","");
        return res;
    }
    @Override
    public int addSubComment(long bvcode, long parentId, long reply, String content, String replyName, String replyUrl, Date commentTime, long userId) {
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            videoId = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            videoId = this.videoMapper.getIdByBVCode(bvcode);
        }
        String tableName = "tb_video_comment" + videoId/10000;
        long parent = parentId;
        int res = videoMapper.addComment(tableName, videoId, userId, content, parent, reply, new Timestamp(commentTime.getTime()),replyName, replyUrl);
        return res;
    }

    @Override
    public VideoVo getVideoById(long bvcode) {
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            videoId = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            if(redisUtils.hasKey(allBVCode) && !this.redisUtils.includeByBloomFilter(bloomFilterHelper,allBVCode,String.valueOf(bvcode))){
                return null;
            }
            videoId = this.videoMapper.getIdByBVCode(bvcode);
        }
        VideoVo vo = videoMapper.getVideoById(videoId);
        if(!this.redisUtils.hExists(playNum, String.valueOf(videoId))){
            redisUtils.hSet(playNum,String.valueOf(videoId), vo.getPlayNum());
        }
        vo.setPlayNum(Long.parseLong(redisUtils.hGet(playNum, String.valueOf(videoId)).toString()));
        return vo;
    }

    @Override
    public List<Map> getComment(long bvcode) {
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            videoId = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            videoId = this.videoMapper.getIdByBVCode(bvcode);
        }
        String tableName = "tb_video_comment" + videoId/10000;
        List<Map> temp = videoMapper.getComment(tableName, videoId);
        Map<Long,Map>map = new HashMap<>();
        for(Map item : temp){
            item.put("subComment",new LinkedList<>());
        }
        Queue<Map> que = new PriorityQueue<>(new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                return o1.get("commentTime").toString().compareTo(o2.get("commentTime").toString());
            }
        });
        for(Map item : temp){
            long id = Long.parseLong(item.get("id").toString());
            long parentId = Long.parseLong(item.get("parentId").toString());
            if(parentId==-1){
                map.put(id, item);
                que.offer(item);
            }else {
                ((List)(map.get(parentId).get("subComment"))).add(item);
            }
        }
        return new ArrayList<>(que);
    }

    @Override
    public ReturnData upload(UploadVideoVo vo){
        int temp = VideoServiceImpl.bvcode.getAndIncrement();
        String uniqueCode = System.currentTimeMillis()/(1000*60) + String.format("%03d", temp);
        this.redisUtils.addByBloomFilter(this.bloomFilterHelper, allBVCode, uniqueCode);
        this.videoMapper.addVideo(vo.getTitle(),vo.getTags(),vo.getUserId(),vo.getDuration(),vo.getLikeNum(),vo.getCommentNum(),vo.getPreview(),vo.getReleaseTime(),
                vo.getStatus(),vo.getCreateTime(),vo.getLmTime(),vo.getCover(),vo.getIntro(),vo.getMd5(), uniqueCode);
        return ReturnData.ok();
    }

    @Override
    public List<VideoVo> getRecommendVideo(int page){
        List<VideoVo> list = videoMapper.getVideoPage(40+0*page);
        return list;
    }
    @Override
    public List<VideoVo> getFirstPageVideo(){
        List<VideoVo> list = videoMapper.getFirstPageVideo();
        return list;
    }

    @Override
    public List<VideoVo> getRecommendVideo(){
        List<VideoVo> videoList = this.videoMapper.getRecomendVideo();
        List<VideoVo> res = new ArrayList<>();
        for(int i=0;i<6 && i<videoList.size();++i)
            res.add(videoList.get(i));
        return res;
    }

    @Override
    public List<VideoVo> getALlVideoByTitle(String title){
        List<VideoVo> videoVoList = this.videoMapper.getALlVideoByTitle(title);
        return videoVoList;
    }

    @Override
    public Map getUserByBVCode(long bvcode){
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvcode)){
            videoId = (Long)redisUtils.get(bvToId+bvcode);
        }else {
            videoId = this.videoMapper.getIdByBVCode(bvcode);
        }
        return this.videoMapper.getUserInfoByUploaderId(videoId);
    }

    @Override
    public int thumb(long userId, long bvCode) {
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvCode)){
            videoId = (Long)redisUtils.get(bvToId+bvCode);
        }else {
            if(redisUtils.hasKey(allBVCode) && !this.redisUtils.includeByBloomFilter(bloomFilterHelper,allBVCode,String.valueOf(bvCode))){
                return -1;
            }
            videoId = this.videoMapper.getIdByBVCode(bvCode);
        }
        long res = -1;
//        24小时内点赞过
        if(redisUtils.hasKey(thumbed+videoId+":"+userId)){
            return (int)res;
        }
//            点赞成功
        res = 1;
        if(!redisUtils.hasKey(thumbNum+videoId)){
            synchronized ((thumbNum+videoId).intern()){
                if(!redisUtils.hasKey(thumbNum+videoId)){
                    long likeNum = videoMapper.getThumbNum(videoId);
                    redisUtils.set(thumbNum+videoId, likeNum);
                }
            }
        }
        redisUtils.incrBy(thumbNum+videoId);

//        记录用户点赞行为，防止刷赞
        redisUtils.set(thumbed+videoId+":"+userId, 1, 86400l);

        return (int)res;
    }

    @Override
    public int unThumb(long userId, long bvCode) {
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvCode)){
            videoId = (Long)redisUtils.get(bvToId+bvCode);
        }else {
            if(redisUtils.hasKey(allBVCode) && !this.redisUtils.includeByBloomFilter(bloomFilterHelper,allBVCode,String.valueOf(bvCode))){
                return -1;
            }
            videoId = this.videoMapper.getIdByBVCode(bvCode);
        }
//        没有点赞过
        if(!redisUtils.hasKey(thumbed+videoId+":"+userId)){
            return -1;
        }
//      取消赞失败
        if(!redisUtils.delKey(thumbed+videoId+":"+userId)){
            return -1;
        }
        if(!redisUtils.hasKey(thumbNum+videoId)){
            synchronized ((thumbNum+videoId).intern()){
                if(!redisUtils.hasKey(thumbNum+videoId)){
                    long likeNum = videoMapper.getThumbNum(videoId);
                    redisUtils.set(thumbNum+videoId, likeNum);
                }
            }
        }
        redisUtils.incrBy(thumbNum+videoId);
        return 1;
    }
    @Override
    public long getThumb(long bvCode){
        long videoId = -1;
        if(this.redisUtils.hasKey(bvToId+bvCode)){
            videoId = (Long)redisUtils.get(bvToId+bvCode);
        }else {
            if(redisUtils.hasKey(allBVCode) && !this.redisUtils.includeByBloomFilter(bloomFilterHelper,allBVCode,String.valueOf(bvCode))){
                return -1;
            }
            videoId = this.videoMapper.getIdByBVCode(bvCode);
        }
        if(!redisUtils.hasKey(thumbNum+videoId)){
            synchronized ((thumbNum+videoId).intern()){
                if(!redisUtils.hasKey(thumbNum+videoId)){
                    long likeNum = videoMapper.getThumbNum(videoId);
                    redisUtils.set(thumbNum+videoId, likeNum);
                    return likeNum;
                }
            }
        }
        return Long.parseLong(redisUtils.get(thumbNum+videoId).toString());
    }
}
