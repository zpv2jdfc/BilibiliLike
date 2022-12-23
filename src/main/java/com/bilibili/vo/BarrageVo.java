package com.bilibili.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BarrageVo implements Serializable {
    @JsonProperty(value = "videoId")
    long videoId;
    @JsonProperty(value = "content")
    String content;
    @JsonProperty(value = "time")
    int time;
}
