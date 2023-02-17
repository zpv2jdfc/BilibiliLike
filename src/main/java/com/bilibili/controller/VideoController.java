package com.bilibili.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.VideoService;
import com.bilibili.vo.*;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.sql.Blob;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.bilibili.common.constant.VideoConstant.uploadStatus;

@RestController
@RequestMapping("video")
public class VideoController {
    @Autowired
    private VideoService videoService;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @PostMapping(value = "upload")
    public Map upLoadVideo( UploadVideoVo vo, @RequestHeader Map<String,String> header){

        vo.setUserId(Integer.valueOf(header.get("id")));
        return videoService.upload(vo);
    }

    @GetMapping(value = "watch/{videoId}")
    public ReturnData getVideo(@PathVariable("videoId") String videoId){
        if(videoId==null || !videoId.matches("[0-9]+")){
            return ReturnData.error(CodeEnum.NO_VIDEO_EXCEPTION.getCode(), CodeEnum.NO_VIDEO_EXCEPTION.getMessage());
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
    public ReturnData getBiu(@RequestParam("videoId")long videoId, @RequestParam("begin")int begin, @RequestParam("end")int end){
        List biuList = this.videoService.getBiu(videoId,begin,end);
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
    public ReturnData addComment(@RequestBody CommentVo vo, @RequestHeader Map<String,String> header) throws ParseException {
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
}
