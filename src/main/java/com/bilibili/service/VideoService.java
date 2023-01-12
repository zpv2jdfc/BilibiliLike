package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.UploadVideoVo;
import com.bilibili.vo.VideoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VideoService {

    default ReturnData uploadWithBlock(UploadVideoVo vo, MultipartFile file){
        return null;
    }
    default Map upload(UploadVideoVo vo, MultipartFile file){
        return null;
    }

    List<Map> getBiu(long id, int begin, int end);
    int addBiu(BarrageVo vo);
    List<Map>getComment(long videoId);
    public int addComment(long videoId, String content, Date commentTime);
    public int addSubComment(long videoId, long parentId, String content, Date commentTime);
    public VideoVo getVideoById(long videoId);
}
