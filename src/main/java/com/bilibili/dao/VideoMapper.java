package com.bilibili.dao;

import com.bilibili.entity.VideoEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {
    VideoEntity getById(long id);

    @Select("select content, biu_time from ${tableName} where video_id=#{id} and biu_time>=#{begin} and biu_time<=#{end}")
    public List<Map> getBiu(@Param("tableName")String tableName, @Param("id")long id, @Param("begin")int begin, @Param("end")int end);

    @Insert("insert into ${tableName} (video_id,user_id,biu_time,content) values(#{videoId},#{userId},#{time},#{content})")
    public int addBiu(@Param("tableName") String tableName, @Param("videoId") long videoId, @Param("content")String content,@Param("userId") long userId, @Param("time")int time);

    @Insert("insert into $(tableName) (video_id,user_id,comment,parent_id,comment_time) values(#{videoId},#{userId},#{content},#{parent},#{commentTime})")
    public int addComment(@Param("tableName")String tableName,
                          @Param("videoId")long videoId,
                          @Param("userId")long userId,
                          @Param("content")String content,
                          @Param("parent")long parent,
                          @Param("commentTime") Date commentTime
                          );

    @Select("select id,user_id,parent_id,comment,like_num,comment_time from ${tableName} where video_id=#{videoId}")
    public List<Map> getComment(@Param("tableName")String tableName, @Param("videoId")long videoId);
}
