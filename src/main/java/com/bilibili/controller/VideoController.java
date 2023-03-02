package com.bilibili.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.BloomFilterHelper;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.config.RedisUtils;
import com.bilibili.dao.VideoMapper;
import com.bilibili.service.RankService;
import com.bilibili.service.VideoService;
import com.bilibili.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Proxy;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.bilibili.common.constant.VideoConstant.uploadStatus;

@Slf4j
@RestController
@RequestMapping("video")
public class VideoController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private RankService rankService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private VideoMapper videoMapper;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String bvToId = "bvToId:";
    @PostMapping(value = "upload")
    public Map upLoadVideo( UploadVideoVo vo, @RequestHeader Map<String,String> header){

        vo.setUserId(Integer.valueOf(header.get("id")));
        return videoService.upload(vo);
    }

    @GetMapping(value = "watch/{videoId}")
    public ReturnData getVideo(@PathVariable("videoId") String videoId, HttpServletRequest request){
//        VideoService serviceProxy = (VideoService) Proxy.newProxyInstance(this.videoService.getClass().getClassLoader(),
//                this.videoService.getClass().getInterfaces(),
//                (a,b,c)->{
//            if(b.getName().equals("getVideoById")){
//                VideoVo vo  = (VideoVo)b.invoke(this.videoService,c);
//
//            }
//            return b.invoke(this.videoService,c);
//        });

        if(videoId==null || !videoId.matches("[0-9]+")){
            return ReturnData.error(CodeEnum.NO_VIDEO_EXCEPTION.getCode(), CodeEnum.NO_VIDEO_EXCEPTION.getMessage());
        }

        String ip = request.getRemoteAddr();
        int ipInt = 0;
        for(String temp : ip.split("\\.")){
            ipInt = ipInt*256 + Integer.parseInt(temp);
        }
        if(!this.redisUtils.sIsMember("watchHistory", videoId+":"+ipInt)) {
            long id = -1;
            if(this.redisUtils.hasKey(bvToId+videoId)){
                id = (Long)redisUtils.get(bvToId+videoId);
            }else {
                id = this.videoMapper.getIdByBVCode(Long.parseLong(videoId));
            }
            this.rankService.watchVideo(id, System.currentTimeMillis());
        }
        VideoVo vo = videoService.getVideoById(Long.parseLong(videoId));
        if(vo==null){
            return ReturnData.error(CodeEnum.NO_VIDEO_EXCEPTION.getCode(), CodeEnum.NO_VIDEO_EXCEPTION.getMessage());
        }
        ReturnData res = ReturnData.ok();
        res.setData(vo);
        return res;
    }
    @GetMapping(value = "getbiu")
    public ReturnData getBiu(@RequestParam("videoId")long bvcode, @RequestParam("begin")int begin, @RequestParam("end")int end){
        List biuList = this.videoService.getBiu(bvcode,begin,end);
        if(biuList!=null){
            ReturnData res = new ReturnData().ok();
            res.setData(biuList);
            return res;
        }
        return ReturnData.error(CodeEnum.NO_VIDEO_EXCEPTION.getCode(), CodeEnum.NO_VIDEO_EXCEPTION.getMessage());
    }
    @PostMapping(value = "biu")
    public ReturnData biu(@RequestBody BarrageVo vo){
        int res = videoService.addBiu(vo);
        return ReturnData.ok();
    }
    @PostMapping(value = "addComment")
    public ReturnData addComment(@RequestBody CommentVo vo, @RequestHeader Map<String,String> header) {
        if(vo.getContent()==null || vo.getContent().length()==0){
            ReturnData res = ReturnData.error(CodeEnum.NO_COMMENT_EXCEPTION.getCode(), CodeEnum.NO_COMMENT_EXCEPTION.getMessage());
            return res;
        }
        long userId = Long.parseLong(header.get("id"));
        int res = videoService.addComment(vo.getVideoId(), vo.getContent(), vo.getCommentTime(), userId);
        return ReturnData.ok();

    }
    @PostMapping(value = "addSubComment")
    public ReturnData addSubComment(@RequestBody CommentVo vo, @RequestHeader Map<String,String> header) throws ParseException {
        if(vo.getContent()==null || vo.getContent().length()==0){
            ReturnData res = ReturnData.error(CodeEnum.NO_COMMENT_EXCEPTION.getCode(), CodeEnum.NO_COMMENT_EXCEPTION.getMessage());
            return res;
        }
        long userId = Long.parseLong(header.get("id"));
        int res = videoService.addSubComment(vo.getVideoId(),vo.getParentId(),vo.getReplyId(), vo.getContent(), vo.getReplyName(), vo.getReplyUrl(), vo.getCommentTime(),userId);
        return ReturnData.ok();

    }
    @GetMapping(value = "getComment")
    public ReturnData getComment(@RequestParam("videoId")long videoId){
        List<Map> data = videoService.getComment(videoId);
        ReturnData res = ReturnData.ok();
        res.setData(data);
        return res;
    }

    @GetMapping(value = "homeScroll")
    public ReturnData getVideos(@RequestParam("page") int page){
        List<VideoVo> list = this.videoService.getRecommendVideo(page);
        ReturnData res = ReturnData.ok();
        res.setData(list);
        return res;
    }

    @GetMapping(value = "firstPageVideo")
    public ReturnData getFirstVideos(){
        List<VideoVo> list = this.videoService.getFirstPageVideo();
        ReturnData res = ReturnData.ok();
        res.setData(list);
        return res;
    }

    @GetMapping(value = "all")
    public ReturnData getAll(@RequestParam("title")String title){
        ReturnData res = new ReturnData().ok();
        List<VideoVo> l = this.videoService.getALlVideoByTitle(title);
        res.setData(l);
        return res;
    }
    @GetMapping(value="hottest")
    public ReturnData getHottest(){
        ReturnData res = new ReturnData().ok();
        List<Map> temp = null;
        try{
            temp = rankService.getHottest();
        }catch (Exception e){
            log.info(e.getMessage());
        }
        List l = new ArrayList();
        if(temp!=null) {
            for (Map item : temp) {
                long id = Long.parseLong(item.get("id").toString());
                long bvCode = this.videoMapper.getBVCodeById(id);
                VideoVo vo = this.videoService.getVideoById(bvCode);
                if(vo==null)
                    continue;
                vo.setTemperature(Double.parseDouble(item.get("score").toString()));
                l.add(vo);
            }
        }
        res.setData(l);
        return res;
    }
    @GetMapping(value = "upInfo")
    public ReturnData getUpInfo(@Param("code")long code, HttpServletRequest request){
        ReturnData res = new ReturnData().ok();

        Map vo = this.videoService.getUserByBVCode(code);
        res.setData(vo);
        return res;
    }
    @PostMapping(value = "thumb")
    public boolean thumb(@Param("userId")long userId,@Param("bvCode")long bvCode){
        return videoService.thumb(userId, bvCode)==1;
    }
    @PostMapping(value = "unthumb")
    public boolean unthumb(@Param("userId")long userId,@Param("bvCode")long bvCode){
        return videoService.unThumb(userId, bvCode)==1;
    }
    @GetMapping(value = "getthumb")
    public ReturnData getthumb(@Param("bvCode")long bvCode){
        long num = videoService.getThumb(bvCode);
        ReturnData res = ReturnData.ok();
        res.setData(num);
        return res;
    }
}
