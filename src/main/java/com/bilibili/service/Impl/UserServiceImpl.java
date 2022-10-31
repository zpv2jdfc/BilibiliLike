package com.bilibili.service.Impl;


import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.dao.UserMapper;
import com.bilibili.entity.UserEntity;
import com.bilibili.service.UserService;
import com.bilibili.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;

    @Override
    public ReturnData getUserInfoById(long id){
        UserEntity userEntity = userMapper.getUserById(id);
        if(userEntity == null){
            return ReturnData.error(CodeEnum.USER_NOTEXIST.getCode(), CodeEnum.USER_NOTEXIST.getMessage());
        }
        ReturnData ret = ReturnData.ok();
        Map map = new HashMap();
        map.put("id", userEntity.getId());
        map.put("nickName", userEntity.getNickName());
        map.put("avatar", userEntity.getAvatar());
        map.put("signature", userEntity.getSingature());
        map.put("level", userEntity.getLevel());
        map.put("privilege", userEntity.getPrivilege());
        map.put("status", userEntity.getStatus());
        map.put("setting", userEntity.getSetting());
        ret.setData(map);
        return ret;
    }
}
