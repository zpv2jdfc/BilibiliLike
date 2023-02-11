package com.bilibili.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FilePojo implements Serializable {
    private static final long serialVersionUID = -6334172193008858856L;

    private Integer id;
    private String path;
    private String name;
    private Long size;
    private String suffix;
    private String type;
    private Integer shareTotal;
    private Integer shareIndex;
    private String key;
}