package com.bilibili.service.Impl;

import com.bilibili.dao.VideoMapper;
import com.bilibili.entity.VideoEntity;
import com.bilibili.vo.VideoVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class VideoServiceImplServiceTest extends BaseServiceTest {
    @Autowired
    VideoMapper videoMapper;
    @Test
    public void getVideoById() {
        long id = 0;
        VideoVo videoEntity = videoMapper.getVideoById(id);
    }
}
