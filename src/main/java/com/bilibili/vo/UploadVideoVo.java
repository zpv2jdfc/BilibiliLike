package com.bilibili.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class UploadVideoVo {
    private String title;

    @JsonProperty(value = "label")
    private String tags;

    private long userId ;
    private int duration;
    private int likeNum=0;
    private int commentNum=0;
    private String preview = "";
    private Timestamp releaseTime;
    private int status = 0;
    private Timestamp createTime;
    private Timestamp LmTime;

    private byte[] cover;

    private String md5;

    @JsonProperty(value = "descript")
    private String intro;
    public UploadVideoVo(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.releaseTime = ts;
        this.createTime = ts;
        this.LmTime = ts;
    }
}
