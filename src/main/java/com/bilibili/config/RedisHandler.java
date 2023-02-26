package com.bilibili.config;

import com.bilibili.common.utils.BloomFilterHelper;
import com.bilibili.dao.VideoMapper;
import com.bilibili.vo.VideoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RedisHandler implements InitializingBean {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private BloomFilterHelper bloomFilterHelper;

    private final String allBVCode = "allBVCode";

    @Override
    public void afterPropertiesSet() throws Exception {
//        预加载视频信息
        List<VideoVo> list = videoMapper.getAllVideo();
        for(VideoVo vo : list){
            redisUtils.addByBloomFilter(bloomFilterHelper, allBVCode, String.valueOf(vo.getBvcode()));
        }
    }
}
