package com.bilibili.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarrageVo implements Serializable {
    @JsonProperty(value = "videoId")
    long videoId;
    @JsonProperty(value = "content")
    String content;
    @JsonProperty(value = "time")
    int time;
}
