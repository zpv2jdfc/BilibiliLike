package com.bilibili.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class VideoVo {
    private long videoId;
    private String videoTitle;
    private BigInteger curUserId;
    private int watchTime;
}
