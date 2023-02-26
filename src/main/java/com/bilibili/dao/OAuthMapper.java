package com.bilibili.dao;

import com.bilibili.entity.OAuthEntity;
import com.bilibili.entity.UserEntity;
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

    @Select("select 1 from oauth where identity_type = 'EMAIL' and identifier = #{email}")
    Integer emailUserExists(@Param("email")String email);

    @Select("select * from user where id = (select user_id from oauth where identity_type = 'EMAIL' and identifier = #{email})")
    @Results(
            value = {
                    @Result(column = "id", property = "id"),
                    @Result(column = "user_nickname", property = "nickName"),
                    @Result(column = "user_avatar", property = "avatar"),
                    @Result(column = "user_singature", property = "signature"),
                    @Result(column = "user_level", property = "level"),
                    @Result(column = "user_privilege", property = "privilege"),
                    @Result(column = "user_status", property = "status"),
                    @Result(column = "user_setting", property = ""),
                    @Result(column = "setting", property = "setting"),
            }
    )
    UserEntity getUserInfoByEmail(@Param("email")String email);
}
