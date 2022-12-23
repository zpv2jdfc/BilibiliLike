package com.bilibili.service.Impl;

import com.bilibili.dao.VideoMapper;
import com.bilibili.entity.VideoEntity;
import com.bilibili.service.VideoService;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.VideoResponseVo;
import com.bilibili.vo.VideoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


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

    @Override
    public List<Map> getBiu(long id, int begin, int end) {
        String tableName = "tb_video_biu" + id/10000;
        return videoMapper.getBiu(tableName,id,begin,end);
    }

    @Override
    public int addBiu(BarrageVo vo) {
        long videoId=vo.getVideoId();
        String tableName = "tb_video_biu" + videoId/10000;
        String content = vo.getContent();
        long userId = 0;
        int time = vo.getTime();
        return videoMapper.addBiu(tableName,videoId,content,userId,time);
    }
}
