package com.bilibili.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class CommentVo {

    @JsonProperty(value = "videoId")
    long videoId;

    @JsonProperty(value = "content")
    String content;

    @JsonProperty(value = "dateTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    Timestamp commentTime;

    @JsonProperty(value = "userId")
    long userId;

//    回复的父对象
    @JsonProperty(value = "parentId")
    long parentId;
//    具体回复人
    @JsonProperty(value = "replyId")
    long replyId;

    @JsonProperty(value = "replyName")
    String replyName;

    @JsonProperty(value = "replyUrl")
    String replyUrl;

}
