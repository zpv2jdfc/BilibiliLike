package com.bilibili.controller;

import com.bilibili.annotation.PerimissionAnnotation;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.config.MQService;
import com.bilibili.config.RedisUtils;
import com.bilibili.dao.VideoMapper;
import com.bilibili.vo.FileVo;
import com.bilibili.vo.VideoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/file")
public class SubmitController {

    @Autowired
    private MQService mqService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private VideoMapper videoMapper;

    @Value("${base.param.tempLocation}")
    private String tempLoc;

    @Value("${base.param.fileLocation}")
    private String fileLoc;

    private final String existFile = "existFile";
    private final String existChunk = "existChunk";
    /**
     * 检查文件是否存在
     * @param md5File
     * @return
     */
    @GetMapping ("checkFile")
    public Boolean checkFile(@RequestParam(value = "md5File") String md5File){
        Boolean exist = false;
        exist = redisUtils.sIsMember(existFile, md5File);
        if(exist)
            return true;
        Integer temp = videoMapper.getVideoByUrl(md5File);
        if(temp==null)
            return false;
        redisUtils.sAdd(existFile, md5File);
        return true;
    }

    /**
     * 检查 分片是否存在
     * @param md5File
     * @param header
     * @return
     */
    @GetMapping("checkChunk")
    @ResponseBody
    public ReturnData checkChunk(@RequestParam(value = "md5File") String md5File, @RequestHeader Map<String,String> header) {
        List l = new ArrayList();
        if(redisUtils.hExists(existChunk, md5File)){
            l = (List) redisUtils.hGet(existChunk, md5File);
        }

        ReturnData res = new ReturnData().ok();
        res.setData(l);
        return res;
    }

    /**
     * 分片上传
     * @param fileVo
     * @param header
     * @return
     */
    @PostMapping(value = "upload")
    @PerimissionAnnotation
    public Boolean uploadChunk(FileVo fileVo, @RequestHeader Map<String,String> header){
        String path = this.tempLoc + fileVo.getMd5File();
        File dirfile = new File(path);
        if (!dirfile.exists()) {//目录不存在，创建目录
            dirfile.mkdirs();
        }
        String chunkName = fileVo.getChunk() + ".tmp";
        String filePath = path+"/"+chunkName;
        File savefile = new File(filePath);
        System.out.println(fileVo.getChunk());
        try {
            if (!savefile.exists()) {
                savefile.createNewFile();//文件不存在，则创建
            }
            fileVo.getFile().transferTo(savefile);//将文件保存
        } catch (IOException e) {
            return false;
        }
        List l = new ArrayList();
        synchronized (fileVo.getMd5File().intern()){
            if(redisUtils.hExists(existChunk, fileVo.getMd5File())){
                l = (List) redisUtils.hGet(existChunk, fileVo.getMd5File());
            }
            l.add(fileVo.getChunk());
            redisUtils.hSet(existChunk, fileVo.getMd5File(), l);
        }

        return true;
    }

    /**
     * 合成分片
     * @param fileVo
     * @param header
     * @return
     * @throws Exception
     */
    @PostMapping(value = "merge")
    @ResponseBody
    public Boolean  merge(@RequestBody FileVo fileVo, @RequestHeader Map<String,String> header) throws Exception {
        redisUtils.sAdd(existFile, fileVo.getMd5File());
        String path = this.tempLoc + fileVo.getMd5File() + "/";
        FileOutputStream fileOutputStream = new FileOutputStream(path+"/complete.mp4");  //合成后的文件
        try {
            byte[] buf = new byte[1024];
            for(long i=0;i<fileVo.getTotal();i++) {
                String chunkFile=i+".tmp";
                File file = new File(path+chunkFile);
                InputStream inputStream = new FileInputStream(file);
                int len = 0;
                while((len=inputStream.read(buf))!=-1){
                    fileOutputStream.write(buf,0,len);
                }
                inputStream.close();
            }
            //合并完成， 开始转码
            mqService.pushOneWayMessage(fileVo.getMd5File());

        } catch (Exception e) {
            return false;
        }finally {
            fileOutputStream.close();
        }
        return true;
    }

}
