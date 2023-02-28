package com.bilibili.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;



@Component
public class MQService {

    @Autowired
    public RocketMQTemplate rocketMQTemplate;


    @Value(value = "${rocketmq.producer.topic}:${rocketmq.producer.sync-tag}")
    private String syncTag;

    @Value(value = "${rocketmq.producer.topic}:${rocketmq.producer.async-tag}")
    private String asyncag;

    @Value(value = "${rocketmq.producer.topic}:${rocketmq.producer.oneway-tag}")
    private String onewayTag;

    @Value(value = "${rocketmq.producer.topic}:")
    private String topic;

    public ResponseMsg push(String tag, String message){
        String messageStr = "order id : " + message;
        Message<String> m = MessageBuilder.withPayload(messageStr)
                .setHeader(RocketMQHeaders.KEYS, message)
                .build();
        rocketMQTemplate.asyncSend(topic+tag, m, new SendCallbackListener(message));
        ResponseMsg msg = new ResponseMsg();
        msg.setSuccessData(null);
        return msg;
    }
    /**
     * 发送异步消息
     *
     * @param id 消息
     * @return 结果
     */
    public ResponseMsg pushAsyncMessage(String id) {

        // 构建消息
        String messageStr = "order id : " + id;
        Message<String> message = MessageBuilder.withPayload(messageStr)
                .setHeader(RocketMQHeaders.KEYS, id)
                .build();
        // 设置发送地和消息信息并发送异步消息
        rocketMQTemplate.asyncSend(asyncag, message, new SendCallbackListener(id));

        ResponseMsg msg = new ResponseMsg();
        msg.setSuccessData(null);
        return msg;
    }

    /**
     * 发送单向消息（不关注发送结果：记录日志）
     *
     * @param id 消息
     * @return 结果
     */
    public ResponseMsg pushOneWayMessage(String id) {

        // 构建消息
        String messageStr = id;
        Message<String> message = MessageBuilder.withPayload(messageStr)
                .setHeader(RocketMQHeaders.KEYS, id)
                .build();
        // 设置发送地和消息信息并发送单向消息
        rocketMQTemplate.sendOneWay(onewayTag, message);

        ResponseMsg msg = new ResponseMsg();
        msg.setSuccessData(null);
        return msg;
    }


    private class SendCallbackListener implements SendCallback {

        private String id;

        public SendCallbackListener(String id) {
            this.id = id;
        }

        @Override
        public void onSuccess(SendResult sendResult) {

        }

        @Override
        public void onException(Throwable throwable) {

        }
    }


    @Data
    public class ResponseMsg {
        public static final int CODE_FAIL = 500;

        public static final int CODE_SUCCESS = 200;

        public static final String MSG_SUCCESS = "success";

        public static final String MSG_FAIL = "fail";

        private int code;

        private String msg;

        private Object data;

        public ResponseMsg() {
            this.code = CODE_FAIL;
            this.msg = MSG_FAIL;
        }

        public void setSuccessData(Object data) {
            this.code = CODE_SUCCESS;
            this.msg = MSG_SUCCESS;
            this.data = data;
        }
    }
}
