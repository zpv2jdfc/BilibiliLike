package com.bilibili.service.Impl;

import com.bilibili.dao.VideoMapper;
import com.bilibili.entity.VideoEntity;
import com.bilibili.service.VideoService;
import com.bilibili.vo.VideoResponseVo;
import com.bilibili.vo.VideoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    VideoMapper videoMapper;
    public VideoResponseVo getVideoById(VideoVo vo){
        if(vo == null)
            return null;
        long id = vo.getVideoId();
        VideoEntity videoEntity = videoMapper.getById(id);
        return null;
    }
}
