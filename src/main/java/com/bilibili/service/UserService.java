package com.bilibili.service;

import com.bilibili.common.utils.ReturnData;
import com.bilibili.vo.UserLoginVo;
import com.bilibili.vo.UserRegisterVo;


public interface UserService {
    default ReturnData login(UserLoginVo vo){
        return null;
    }
    default ReturnData register(UserRegisterVo vo){
        return null;
    }
}
