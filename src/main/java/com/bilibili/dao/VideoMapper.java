package com.bilibili.dao;

import com.bilibili.entity.VideoEntity;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.UserProfileVo;
import com.bilibili.vo.VideoVo;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.sql.Blob;
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
                    @Result(column = "bvcode", property = "videoId"),
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
                    @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
                            one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                    )
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
    @Insert("insert into ${tableName} (video_id,user_id,comment,parent_id,comment_time,reply_id,reply_name,reply_url) values(#{videoId},#{userId},#{content},#{parent},#{commentTime},#{reply},#{replyName},#{replyUrl})")
    public int addComment(@Param("tableName")String tableName,
                          @Param("videoId")long videoId,
                          @Param("userId")long userId,
                          @Param("content")String content,
                          @Param("parent")long parent,
                          @Param("reply") long reply,
                          @Param("commentTime") Timestamp commentTime,
                          @Param("replyName")String replyName,
                          @Param("replyUrl")String replyUrl
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
                    @Result(column = "reply_name", property = "replyName", javaType = String.class),
                    @Result(column = "reply_url", property = "replyUrl", javaType = String.class),
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

//    用户上传视频
    @Insert("insert into tb_video (title,tags,user_id,duration,like_num,comment_num,preview,release_time,status,create_time,lm_time,cover,url,bvcode) " +
            "values (#{title},#{tags},#{userId},#{duration},#{likeNum},#{commentNum},#{preview},#{releaseTime},#{status},#{createTime},#{lmTime},#{cover},#{md5},#{bvCode})")
    public int addVideo(@Param("title")String title,
                        @Param("tags")String tags,
                        @Param("userId")long userId,
                        @Param("duration")int duration,
                        @Param("likeNum")int likeNum,
                        @Param("commentNum")int commentNum,
                        @Param("preview")String preview,
                        @Param("releaseTime")Timestamp releaseTime,
                        @Param("status")int status,
                        @Param("createTime")Timestamp createTime,
                        @Param("lmTime")Timestamp lmTime, @Param("cover") byte[] cover, @Param("intro") String intro,@Param("md5") String md5, @Param("bvCode")String bvCode
                        );
//    分页查询视频
    @Select("select * from tb_video order by like_num desc limit #{start},20")
    @Results(
            value = {
                @Result(column = "bvcode", property = "videoId", id = true),
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
                @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
                        one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                )
            }
    )
    public List<VideoVo> getVideoPage(@Param("start")int start);
//    第一页的视频
@Select("select * from tb_video order by like_num desc limit 40")
@Results(
        value = {
                @Result(column = "bvcode", property = "videoId", id = true),
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
                @Result(column = "cover", property = "cover"),
                @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
                        one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                )
        }
)
public List<VideoVo> getFirstPageVideo();

//推荐视频
    @Select("select * from tb_recommend")
    @Results(
            value = {
                    @Result(column = "bvcode", property = "videoId", id = true),
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
                    @Result(column = "cover", property = "cover"),
                    @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
                            one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                    )
            }
    )
    public List<VideoVo> getRecomendVideo();

//    根据用户id获取视屏
//    根据id获取视频信息
@Select("select * from tb_video where user_id = #{id}")
@Results(
        value = {
                @Result(column = "bvcode", property = "videoId", id = true),
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
    List<VideoVo> getVideoByUserId(@Param("id") long userId);

//根据标题搜索视频
    @Select("select * from tb_video where title like '%${title}%'")
    @Results(
            value = {
                    @Result(column = "bvcode", property = "videoId", id = true),
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
                    @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
                            one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                    )
            }
    )
    List<VideoVo> getALlVideoByTitle(@Param("title")String title);

    /**
     * bvcode获取上传该视频的用户
     * @param videoId
     * @return
     */
    @Select("select user_id from tb_video where id = #{videoId}")
    @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
            one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById"))
    public Map getUserInfoByUploaderId(@Param("videoId")long videoId);

    @Select("select 1 from tb_video where url = #{url} limit 1")
//    public List<VideoVo> getVideoByUrl(@Param("url")String url);
    public Integer getVideoByUrl(@Param("url")String url);

    @Update("update tb_video set play_num = #{playNum} where id = #{videoId}")
    public int updatePlayNum(@Param("videoId")long videoId, @Param("playNum")int playNum);

    @Select("select id from tb_video where bvcode =  #{bvcode}")
    public long getIdByBVCode(@Param("bvcode")long bvcode);

    @Select("select bvcode from tb_video where id =  #{id}")
    public long getBVCodeById(@Param("id")long id);

//    预加载
    @Select("select * from tb_video")
    @Results(
            value = {
                    @Result(column = "id", property = "videoId"),
                    @Result(column = "bvcode", property = "bvcode"),
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
                    @Result(column = "user_id", property = "owner", javaType = UserProfileVo.class,
                            one = @One(select = "com.bilibili.dao.VideoMapper.getUserProfileById")
                    )
            }
    )
    public List<VideoVo> getAllVideo();

    @Insert("<script>" +
            "insert into ${tableName}" +
            "(video_id,content,biu_time)" +
            "values" +
            "<foreach item='item' index='index' collection='list' separator=','> " +
            "(" +
            "#{item.videoId},#{item.content},#{item.time}" +
            ")" +
            "</foreach>" +
            "</script>")
    public Integer addBarrages(@Param("tableName")String tableName,@Param("list") List<BarrageVo> list);
}
