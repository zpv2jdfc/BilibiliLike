package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.UploadVideoVo;
import com.bilibili.vo.UserProfileVo;
import com.bilibili.vo.VideoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VideoService {

    default ReturnData uploadWithBlock(UploadVideoVo vo, MultipartFile file){
        return null;
    }
    default ReturnData upload(UploadVideoVo vo){
        return null;
    }

    List<Map> getBiu(long id, int begin, int end);
    int addBiu(BarrageVo vo);
    List<Map>getComment(long videoId);
    public int addComment(long videoId, String content, Date commentTime, long userId);
    public int addSubComment(long videoId, long parentId, long reply, String content, String replyName, String replyUrl, Date commentTime, long userId);
    public VideoVo getVideoById(long videoId);

    public List<VideoVo> getRecommendVideo(int page);
    public List<VideoVo> getFirstPageVideo();

    public List<VideoVo> getRecommendVideo();
    public List<VideoVo> getALlVideoByTitle(String title);
    public Map getUserByBVCode(long BVCode);
}
