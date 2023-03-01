package com.bilibili.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThumbMapper {

    @Insert("insert into tb_thumb (video_id,user_id,ops,thumb_num) values(#{videoId},#{userId},#{ops},#{num})")
    public int addRecord(@Param("videoId")long videoId, @Param("userId")long userId, @Param("ops")boolean ops, @Param("num")int num);

//    @Insert("<script> insert into tb_thumb (video_id,user_id,ops,thumb_num) values ()")
//    public int addRecords(@Param("list")List list);
}
