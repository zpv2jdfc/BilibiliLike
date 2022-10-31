package com.bilibili.dao;

import com.bilibili.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public UserEntity getUserById(@Param("id")long id);
    public UserEntity getOtherInfoById(@Param("id")long id);
    public UserEntity getOtherInfoByName(@Param("name")String name);
}
