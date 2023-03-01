package com.bilibili.service;

import com.bilibili.config.MQService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "${rocketmq.consumer.topic}:redisMessage")
public class CacheMessageService implements RocketMQListener<String> {
    @Autowired
    private MQService mqService;

    @Override
    public void onMessage(String message) {
        String[] temp = message.split(";");
        long videoId = Long.parseLong(temp[0]);
        long userId = Long.parseLong(temp[1]);
        boolean ops = Boolean.parseBoolean(temp[2]);
        int num = Integer.parseInt(temp[3]);
    }
}
