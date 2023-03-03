package com.bilibili.service;

import com.bilibili.config.RedisUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RocketMQMessageListener(
        consumerGroup = "${rocketmq.consumer.group}",  // 消费组，格式：namespace全称%group名称
        // 需要使用topic全称，所以进行topic名称的拼接，也可以自己设置  格式：namespace全称%topic名称
        topic = "msgExpireTopic"
)
public class MessageService implements RocketMQListener<String> {
    //  已经发送的消息列表
    private final static String sendedMessage = "sendedMessage:";
    @Autowired
    private RedisUtils redisUtils;
    @Override
    public void onMessage(String message) {
        String uuid = message;
        String key = sendedMessage+uuid;
        if(!redisUtils.hasKey(key)){
//            消息已经得到确认， 无需记录到离线消息列表
        }else{
//            消息还在发送中列表中， 超时了， 放到离线消息列表
            redisUtils.delKey(key);
            String to = message.split(":")[2];
            redisUtils.rPush(message+to, message);
            redisUtils.expire(message+to, 7 , TimeUnit.DAYS);
        }
    }

}
