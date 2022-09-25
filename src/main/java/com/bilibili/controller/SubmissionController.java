package com.bilibili.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.service.SubmissionService;
import com.bilibili.vo.SubmissionResponseVo;
import com.bilibili.vo.UploadVideoVo;
import com.bilibili.vo.UserResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import static com.bilibili.common.constant.SubmissionConstant.*;


@Controller
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @PostMapping(value = "video")
    public String upLoadVideo(@RequestParam("file") MultipartFile file, String jsonUploadVideoVo, HttpSession session){
        if(file.isEmpty()){
            return null;
        }
        UploadVideoVo vo = JSON.parseObject(jsonUploadVideoVo, new TypeReference<UploadVideoVo>(){});
        //尝试上传视频对象
        ReturnData returnData = submissionService.upload(vo, file);
        SubmissionResponseVo ret = returnData.getData(new TypeReference<SubmissionResponseVo>(){});
        session.setAttribute(uploadStatus,ret);
        return null;
    }
}
