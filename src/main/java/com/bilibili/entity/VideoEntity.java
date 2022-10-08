package com.bilibili.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class VideoEntity implements Serializable {
    private long id;
    private String title;
    private String tags;
    private long userId;
    private long duration;
    private long likeNum;
    private long commentNum;
    private String preview;
    private java.sql.Timestamp releaseTime;
    private long status;
    private java.sql.Timestamp createTime;
    private java.sql.Timestamp lmTime;
}
