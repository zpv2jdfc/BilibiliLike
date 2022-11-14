package com.bilibili.dao;

import com.bilibili.entity.OAuthEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OAuthMapper {

    long createUser(@Param("userid") long id,
                            @Param("identitytype") String identitytype,
                            @Param("identifier") String identifier,
                            @Param("credential") String credential);
    /**
     * id获取或用认证信息
     * 管理员用
     */
    @Select("select user_id, identifier, credential from oauth where id = #{id} and identity_type = #{identityType}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "identifier", column = "identifier"),
            @Result(property = "credential", column = "credential"),
    })
    OAuthEntity getUserByUserId(@Param("entity") OAuthEntity entity);

    /**
     * 用户登录认证
     * @param entity
     * @return
     */
    @Select("select user_id, identifier, credential from oauth where identity_type = #{entity.identityType} and identifier = #{entity.identifier}")
    @Results({
            @Result(property = "userId", column = "user_id"),
            @Result(property = "identifier", column = "identifier"),
            @Result(property = "credential", column = "credential"),
    })
    OAuthEntity getUserByCredential(@Param("entity") OAuthEntity entity);
}
