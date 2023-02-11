package com.bilibili.service.Impl;

import com.bilibili.dao.VideoMapper;
import com.bilibili.service.VideoService;
import com.bilibili.vo.BarrageVo;
import com.bilibili.vo.VideoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    VideoMapper videoMapper;

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");



    @Override
    public List<Map> getBiu(long id, int begin, int end) {
        String tableName = "tb_video_biu" + id/10000;
        return videoMapper.getBiu(tableName,id,begin,end);
    }

    @Override
    public int addBiu(BarrageVo vo) {
        long videoId=vo.getVideoId();
        String tableName = "tb_video_biu" + videoId/10000;
        String content = vo.getContent();
        long userId = 0;
        int time = vo.getTime();
        return videoMapper.addBiu(tableName,videoId,content,userId,time);
    }


    @Override
    public int addComment(long videoId, String content, Date commentTime) {
        String tableName = "tb_video_comment" + videoId/10000;
        long userId = 0;
        long parent = -1;

        int res = videoMapper.addComment(tableName, videoId, userId, content, parent, -1, new Timestamp(commentTime.getTime()),"","");
        return res;
    }
    @Override
    public int addSubComment(long videoId, long parentId, long reply, String content, String replyName, String replyUrl, Date commentTime) {
        String tableName = "tb_video_comment" + videoId/10000;
        long userId = 0;
        long parent = parentId;
        int res = videoMapper.addComment(tableName, videoId, userId, content, parent, reply, new Timestamp(commentTime.getTime()),replyName, replyUrl);
        return res;
    }

    @Override
    public VideoVo getVideoById(long videoId) {
        VideoVo vo = videoMapper.getVideoById(videoId);
        return vo;
    }

    @Override
    public List<Map> getComment(long videoId) {
        String tableName = "tb_video_comment" + videoId/10000;
        List<Map> temp = videoMapper.getComment(tableName, videoId);
        Map<Long,Map>map = new HashMap<>();
        for(Map item : temp){
            item.put("subComment",new LinkedList<>());
        }
        Queue<Map> que = new PriorityQueue<>(new Comparator<Map>() {
            @Override
            public int compare(Map o1, Map o2) {
                return o1.get("commentTime").toString().compareTo(o2.get("commentTime").toString());
            }
        });
        for(Map item : temp){
            long id = Long.parseLong(item.get("id").toString());
            long parentId = Long.parseLong(item.get("parentId").toString());
            if(parentId==-1){
                map.put(id, item);
                que.offer(item);
            }else {
                ((List)(map.get(parentId).get("subComment"))).add(item);
            }
        }
        return new ArrayList<>(que);
    }
}
