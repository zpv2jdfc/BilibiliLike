package com.bilibili.dao;

import com.bilibili.entity.VideoEntity;
import com.bilibili.vo.UserProfileVo;
import com.bilibili.vo.VideoVo;
import org.apache.ibatis.annotations.*;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {

//    根据id获取视频信息
    @Select("select * from tb_video where id = #{id}")
    @Results(
            value = {
                    @Result(column = "id", property = "videoId", id = true),
                    @Result(column = "title", property = "videoTitle"),
                    @Result(column = "user_id", property = "userId"),
                    @Result(column = "duration", property = "duration"),
                    @Result(column = "url", property = "url"),
                    @Result(column = "like_num", property = "likeNum"),
                    @Result(column = "play_num", property = "playNum"),
                    @Result(column = "biu_num", property = "biuNum"),
                    @Result(column = "comment_num", property = "commentNum"),
                    @Result(column = "release_time", property = "upTime"),
                    @Result(column = "tags", property = "tags"),
                    @Result(column = "intro", property = "intro"),
            }
    )
    VideoVo getVideoById(@Param("id") long id);

//    获取弹幕
    @Select("select content, biu_time from ${tableName} where video_id=#{id} and biu_time>=#{begin} and biu_time<=#{end}")
    public List<Map> getBiu(@Param("tableName")String tableName, @Param("id")long id, @Param("begin")int begin, @Param("end")int end);

//    新增弹幕
    @Insert("insert into ${tableName} (video_id,user_id,biu_time,content) values(#{videoId},#{userId},#{time},#{content})")
    public int addBiu(@Param("tableName") String tableName, @Param("videoId") long videoId, @Param("content")String content,@Param("userId") long userId, @Param("time")int time);

//    新增评论
    @Insert("insert into ${tableName} (video_id,user_id,comment,parent_id,comment_time,reply_id) values(#{videoId},#{userId},#{content},#{parent},#{commentTime},#{reply})")
    public int addComment(@Param("tableName")String tableName,
                          @Param("videoId")long videoId,
                          @Param("userId")long userId,
                          @Param("content")String content,
                          @Param("parent")long parent,
                          @Param("reply") long reply,
                          @Param("commentTime") Timestamp commentTime
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
                    @Result(column = "comment_time", property = "commentTime", javaType = String.class),
                    @Result(column = "user_id", property = "user", javaType = UserProfileVo.class,
                    one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")),
                    @Result(column = "reply_id", property = "replyTo", javaType = UserProfileVo.class,
                    one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                    )
            }
    )
    public List<Map> getComment(@Param("tableName")String tableName, @Param("videoId")long videoId);

//    显示在评论区的用户基本信息
    @Select("select id, user_nickname, user_avatar, user_singature, user_level, user_privilege, user_status, concat('profile/', id) as user_url from user where id = #{userId}")
    @Results(
            value = {
                    @Result(column = "id", property = "id", id = true),
                    @Result(column = "user_nickname", property = "name"),
                    @Result(column = "user_avatar", property = "avatar"),
                    @Result(column = "user_singature", property = "singature"),
                    @Result(column = "user_level", property = "level"),
                    @Result(column = "user_privilege", property = "privilege"),
                    @Result(column = "user_status", property = "status"),
                    @Result(column = "user_url", property = "url"),
            }
    )
    public UserProfileVo getUserProfileById(@Param("userId")long userId);

}
