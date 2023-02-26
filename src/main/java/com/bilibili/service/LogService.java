package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.vo.UserLoginVo;

public interface LogService {
//    default ReturnData createUser(String name){return null;}
    ReturnData createUser(String name, String identitytype, String identifier, String credential);

    ReturnData login(UserLoginVo vo);

    public boolean sendVerifyMail(String to);
    ReturnData mailRegist(String email, String password);
}
