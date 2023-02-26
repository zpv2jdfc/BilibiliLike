package com.bilibili.service.Impl;

import com.bilibili.common.utils.JWTUtil;
import com.bilibili.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

public class MessageServiceImpl implements MessageService {
    @Data
    class MessageBody{
        private String from;
        private String to;
        private String message;

    }
    public String handleMessage(String message){
        ObjectMapper mapper = new ObjectMapper();
        MessageBody body;
        try{
            body = mapper.readValue(message, MessageBody.class);
            long fromId = JWTUtil.getUID(body.getFrom());
            if(fromId<0)
                throw new RuntimeException();
            long toId = Long.parseLong(body.getTo());
        }catch (Exception e){
            return null;
        }
        return null;

    }
}
