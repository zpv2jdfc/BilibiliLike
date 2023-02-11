package com.bilibili.vo;

import com.bilibili.entity.VideoEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class FileVo implements Serializable {
    private static final long serialVersionUID = -4528742454491886780L;

    @JsonProperty(value = "file")
    private MultipartFile file;

    @JsonProperty(value = "md5File")
    private String md5File;

    @JsonProperty(value = "chunk")
    private int chunk;

    @JsonProperty(value = "total")
    private int total;
}
