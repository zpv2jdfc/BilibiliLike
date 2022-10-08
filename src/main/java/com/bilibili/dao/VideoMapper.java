package com.bilibili.dao;

import com.bilibili.entity.VideoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

@Mapper
public interface VideoMapper {
    VideoEntity getById(long id);
}
