package com.bilibili.service.Impl;

import com.bilibili.dao.VideoMapper;
import com.bilibili.entity.VideoEntity;
import com.bilibili.service.VideoService;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.VideoVo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class VideoServiceImplServiceTest extends BaseServiceTest {
    @Autowired
    VideoMapper videoMapper;
    @Autowired
    private VideoService videoService;
    @Test
    public void getVideoById() {
        long id = 0;
        VideoVo videoEntity = videoMapper.getVideoById(id);
    }

    @Test
    public void testSendBiu(){
        BarrageVo vo = new BarrageVo(27956329867l,"qwertyuiiop",2);
        videoService.addBiu(vo);
    }

    @Test
    public void testGetBiu(){
        videoService.getBiu(27956329867l,0,20);
    }
}
