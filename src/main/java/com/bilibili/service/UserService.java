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
    /**用户获取自己的信息 **/
    default ReturnData getUserInfoById(long id){
        return null;
    }
    /**修改个人信息**/
    default ReturnData updateUserInfo(long id, String fieldName, String newValue){return null;}
    /** 用户查看其他人的基本信息 **/
    default ReturnData getOtherInfoById(long id){return null;}
    default ReturnData getOtherInfoByName(String name){return null;}
}
