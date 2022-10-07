package com.bilibili.dao;

import com.bilibili.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public UserEntity getUserById(@Param("id")String id);
}
