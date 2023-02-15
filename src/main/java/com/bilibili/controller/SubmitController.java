package com.bilibili.controller;

import com.bilibili.config.MQService;
import com.bilibili.vo.FileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class SubmitController {

    @Autowired
    private MQService mqService;

    @Value("${base.param.tempLocation}")
    private String tempLoc;

    @Value("${base.param.fileLocation}")
    private String fileLoc;

    /**
     * 检查文件是否存在
     * @param md5File
     * @return
     */
    @PostMapping("checkFile")
    public Boolean checkFile(@RequestParam(value = "md5File") String md5File){
        Boolean exist = false;

        //实际项目中，这个md5File唯一值，应该保存到数据库或者缓存中，通过判断唯一值存不存在，来判断文件存不存在，这里我就不演示了
		/*if(true) {
			exist = true;
		}*/
        return exist;
    }

    /**
     * 检查 分片是否存在
     * @param fileVo
     * @param header
     * @return
     */
    @PostMapping("checkChunk")
    @ResponseBody
    public Boolean checkChunk(@RequestBody FileVo fileVo, @RequestHeader Map<String,String> header) {
        Boolean exist = false;
        String path = this.tempLoc + fileVo.getMd5File() + "/";//分片存放目录
        String chunkName =  fileVo.getChunk()+ ".tmp";//分片名
        File file = new File(path+chunkName);
        if (file.exists()) {
            exist = true;
        }
        return exist;
    }

    /**
     * 分片上传
     * @param fileVo
     * @param header
     * @return
     */
    @PostMapping(value = "upload")
    public Boolean uploadChunk(FileVo fileVo, @RequestHeader Map<String,String> header){
        String path = this.tempLoc + fileVo.getMd5File();
        File dirfile = new File(path);
        if (!dirfile.exists()) {//目录不存在，创建目录
            dirfile.mkdirs();
        }
        String chunkName = fileVo.getChunk() + ".tmp";
        String filePath = path+"/"+chunkName;
        File savefile = new File(filePath);
        try {
            if (!savefile.exists()) {
                savefile.createNewFile();//文件不存在，则创建
            }
            fileVo.getFile().transferTo(savefile);//将文件保存
        } catch (IOException e) {
            return false;
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
