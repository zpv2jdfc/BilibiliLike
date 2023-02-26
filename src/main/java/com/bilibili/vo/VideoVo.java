package com.bilibili.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class VideoVo {
    @JsonProperty(value = "id")
    private long videoId;
    @JsonProperty(value = "title")
    private String videoTitle;
    private long userId;
    private int duration;
    private String url;
    private long likeNum;
    private long playNum;
    private int biuNum;
    private int commentNum;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date upTime;
    private String tags;
    private String intro;
    private String cover;
    private UserProfileVo owner;
    private double temperature = 0;
    private long bvcode;
}
