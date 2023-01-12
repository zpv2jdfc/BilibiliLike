package com.bilibili.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
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
    private int likeNum;
    private int playNum;
    private int biuNum;
    private int commentNum;
    private Date upTime;
    private String tags;
    private String intro;
}
