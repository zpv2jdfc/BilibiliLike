package com.bilibili.dao;

import com.bilibili.entity.VideoEntity;
import com.bilibili.vo.UserProfileVo;
import com.bilibili.vo.VideoVo;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {

    @Select("select * from tb_video where id = #{id}")
    @Results(
            value = {
                    @Result(column = "id", property = "videoId", id = true),
                    @Result(column = "title", property = "videoTitle"),
                    @Result(column = "user_id", property = "userId"),
                    @Result(column = "duration", property = "duration"),
                    @Result(column = "url", property = "url"),
                    @Result(column = "likeNum", property = "likeNum"),
                    @Result(column = "playNum", property = "playNum"),
                    @Result(column = "biuNum", property = "biuNum"),
                    @Result(column = "commentNum", property = "commentNum"),
                    @Result(column = "upTime", property = "upTime"),
                    @Result(column = "tags", property = "tags"),
                    @Result(column = "intro", property = "intro"),
            }
    )
    VideoVo getVideoById(@Param("id") long id);

    @Select("select content, biu_time from ${tableName} where video_id=#{id} and biu_time>=#{begin} and biu_time<=#{end}")
    public List<Map> getBiu(@Param("tableName")String tableName, @Param("id")long id, @Param("begin")int begin, @Param("end")int end);

    @Insert("insert into ${tableName} (video_id,user_id,biu_time,content) values(#{videoId},#{userId},#{time},#{content})")
    public int addBiu(@Param("tableName") String tableName, @Param("videoId") long videoId, @Param("content")String content,@Param("userId") long userId, @Param("time")int time);

    @Insert("insert into ${tableName} (video_id,user_id,comment,parent_id,comment_time) values(#{videoId},#{userId},#{content},#{parent},#{commentTime})")
    public int addComment(@Param("tableName")String tableName,
                          @Param("videoId")long videoId,
                          @Param("userId")long userId,
                          @Param("content")String content,
                          @Param("parent")long parent,
                          @Param("commentTime") String commentTime
                          );


//    视频评论
    @Select("select id,user_id,parent_id,comment,like_num,comment_time" +
            " from ${tableName} where video_id=#{videoId}")
    @Results(
            value = {
                    @Result(column = "id", property = "id", id = true),
                    @Result(column = "user_id", property = "userId"),
                    @Result(column = "parent_id", property = "parentId"),
                    @Result(column = "comment", property = "comment"),
                    @Result(column = "like_num", property = "likeNum"),
                    @Result(column = "comment_time", property = "commentTime"),
                    @Result(column = "user_id", property = "user", javaType = UserProfileVo.class,
                    one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById"))
            }
    )
    public List<Map> getComment(@Param("tableName")String tableName, @Param("videoId")long videoId);

//    显示在评论区的用户基本信息
    @Select("select id, user_nickname, user_avatar, user_singature, user_level, user_privilege, user_status from user where id = #{userId}")
    @Results(
            value = {
                    @Result(column = "id", property = "id", id = true),
                    @Result(column = "user_nickname", property = "name"),
                    @Result(column = "user_avatar", property = "avatar"),
                    @Result(column = "user_singature", property = "singature"),
                    @Result(column = "user_level", property = "level"),
                    @Result(column = "user_privilege", property = "privilege"),
                    @Result(column = "user_status", property = "status"),
            }
    )
    public UserProfileVo getUserProfileById(@Param("userId")long userId);
}
