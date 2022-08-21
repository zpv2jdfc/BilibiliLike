package com.bilibili.dao;

import com.bilibili.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    public User getUserById(@Param("id")String id);
}
