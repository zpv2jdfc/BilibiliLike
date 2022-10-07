package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.vo.UploadVideoVo;
import com.bilibili.vo.VideoResponseVo;
import com.bilibili.vo.VideoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VideoService {

    default ReturnData uploadWithBlock(UploadVideoVo vo, MultipartFile file){
        return null;
    }
    default Map upload(UploadVideoVo vo, MultipartFile file){
        return null;
    }
    default VideoResponseVo getVideoById(VideoVo vo){
        return null;
    }
    default List<VideoResponseVo> getVideoList(List<VideoVo> vo){
        return null;
    }
}
