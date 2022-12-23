package com.bilibili.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.VideoService;
import com.bilibili.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

import static com.bilibili.common.constant.VideoConstant.uploadStatus;

@RestController
@RequestMapping("video")
public class VideoController {
    @Autowired
    private VideoService videoService;

    @PostMapping(value = "upload")
    public Map upLoadVideo(@RequestParam("file") MultipartFile file, String jsonUploadVideoVo, HttpSession session){
        if(file.isEmpty()){
            return null;
        }
        UploadVideoVo vo = JSON.parseObject(jsonUploadVideoVo, new TypeReference<UploadVideoVo>(){});
        //尝试上传视频对象
        Map ret = videoService.upload(vo, file);
        return ret;
    }

    @PostMapping(value = "watch")
    public ReturnData getVideo(String jsonVideoVo, HttpSession session){
        VideoVo vo = JSON.parseObject(jsonVideoVo, new TypeReference<VideoVo>(){});
        VideoResponseVo ret = videoService.getVideoById(vo);
        if(ret != null){
            return ReturnData.ok().setData(ret);
        }
        return ReturnData.error(CodeEnum.NO_VIDEO_EXCEPTION.getCode(), CodeEnum.NO_VIDEO_EXCEPTION.getMessage());
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
}
