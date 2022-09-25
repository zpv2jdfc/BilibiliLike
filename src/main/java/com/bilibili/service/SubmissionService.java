package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.vo.UploadVideoVo;
import org.springframework.web.multipart.MultipartFile;

public interface SubmissionService {

    default ReturnData uploadWithBlock(UploadVideoVo vo, MultipartFile file){
        return null;
    }
    default ReturnData upload(UploadVideoVo vo, MultipartFile file){
        return null;
    }
}
