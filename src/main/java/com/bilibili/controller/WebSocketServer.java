package com.bilibili.controller;


import com.alibaba.fastjson.JSON;
import com.bilibili.config.MQService;
import com.bilibili.config.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/websocket/{sid}")
public class WebSocketServer {

    private static RedisUtils redisUtils;

    private static MQService mqService;

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils){
        WebSocketServer.redisUtils = redisUtils;
    }
    @Autowired
    public void setMqService(MQService mqService){
        WebSocketServer.mqService = mqService;
    }

    private static ConcurrentHashMap<String, WebSocketServer> webSocketServerMAP = new ConcurrentHashMap<>();
    private Map<String,String> msgMap = new HashMap<>();

    private final static String online = "online";
    private final static String ack = "ack";
    private final static String message = "message:";
//  已经发送的消息列表
    private final static String sendedMessage = "sendedMessage:";


    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid = "";
    private String uri;

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) throws IOException {
        log.info(sid+" 建立连接");
        this.session = session;
        this.sid = sid;
        this.uri = session.getRequestURI().toString();
        WebSocketServer webSocketServer = webSocketServerMAP.get(sid);
        if(webSocketServer != null){ //同样业务的连接已经在线，则把原来的挤下线。
//            webSocketServer.session.getBasicRemote().sendText(sid + "重复连接被挤下线了");
            webSocketServer.session.close();//关闭连接，触发关闭连接方法onClose()
        }
        webSocketServerMAP.put(sid, this);//保存uri对应的连接服务
    }

    /**
     * 连接关闭时触发，注意不能向客户端发送消息了
     * @throws IOException
     */
    @OnClose
    public void onClose() throws IOException {
        webSocketServerMAP.remove(uri);//删除uri对应的连接服务
        reduceOnlineCount(); // 在线数减1
    }

    /**
     * online
     * ack:msgId
     * send:from:to:content
     * @param message
     */
    @OnMessage
    public  void onMessage(String message) {
        log.info("收到消息：" + message);
        if(message.equals(online)){
            // 用户上线，发送离线消息
            if(redisUtils.hasKey(this.message+sid)){
                while(redisUtils.lLen(this.message+sid)>0){
                    sendMsg(redisUtils.lPop(this.message+sid).toString());
                }
            }
        }else if(message.split(":")[0].equals(ack)){
            // 用户确认收到消息
            String msgKey = message.split(":")[1];
            redisUtils.delKey(sendedMessage+msgKey);
        }else {
            // 用户发送私信
            sendMsg(message);
        }
    }
    private void sendMsg(String msg){
        String fromId = sid;
        String toId = msg.split(":")[2];
        if(webSocketServerMAP.get(toId)==null){
//            接收方不在线， 消息缓存在redis中.  为每个用户建立一个消息列表
            redisUtils.rPush(message+toId, msg);
            redisUtils.expire(message+toId, 7 , TimeUnit.DAYS);
        }else {
//            接收方在线， 尝试发送消息
            UUID uuid = UUID.randomUUID();
            redisUtils.set(sendedMessage+uuid, msg);
//            消息队列发送延时消息， 1分钟后检查有没有收到确认
            mqService.pushDelayMessage(uuid.toString(), "checkovertime");
            trySend(fromId, toId, msg+":"+uuid);
        }
    }
    private void trySend(String from, String to, String fullMsg){
        try{
            webSocketServerMAP.get(to).session.getAsyncRemote().sendText(fullMsg);
        }catch (Exception e){
            log.info(e.getMessage());
        }
    }
    @OnError
    public void onError(Session session, Throwable error) {
        try {
            log.info("{}:通信发生错误，连接关闭",sid);
            webSocketServerMAP.remove(uri);//删除uri对应的连接服务
        }catch (Exception e){
        }
    }


    private void addOnlineCount(){

    }
    private void reduceOnlineCount(){

    }

}
