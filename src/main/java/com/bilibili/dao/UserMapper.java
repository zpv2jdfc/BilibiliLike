package com.bilibili.dao;

import com.bilibili.entity.UserEntity;
import com.bilibili.vo.UserInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    public UserEntity getUserById(@Param("id")long id);
    public UserEntity getOtherInfoById(@Param("id")long id);
    public UserEntity getOtherInfoByName(@Param("name")String name);
    public int createUser(UserEntity userEntity);

    @Select("select 1 from user where id = #{id} limit 1")
    public int checkExist(@Param("id")long id);
    @Update("update user set ${fieldname} = #{newvalue}, lm_time = #{time}  where id = #{id}")
    public int updateUser(@Param("id")long id,@Param("fieldname")String fieldname,@Param("newvalue")String newvalue,@Param("time")java.util.Date time);

    @Update("update user set user_nickname = #{user.name}, user_singature = #{user.signature} where id = #{user.id}")
    public int updateUserInfo(@Param("user")UserInfoVo vo);

    @Update("update user set user_avatar = #{user.avatar} where id = #{user.id}")
    public int updateAvatar(@Param("user")UserInfoVo vo);
}
