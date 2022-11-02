package com.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OAuthMapper {

    long createUser(@Param("userid") long id,
                            @Param("identitytype") String identitytype,
                            @Param("identifier") String identifier,
                            @Param("credential") String credential);
}
