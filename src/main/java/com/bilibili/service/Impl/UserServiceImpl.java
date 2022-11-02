package com.bilibili.service.Impl;


import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.dao.UserMapper;
import com.bilibili.entity.UserEntity;
import com.bilibili.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    private Map<String, String> entityToTable = new HashMap<>();
    private static final String nickname = "nickname";
    private static final String id = "id";
    private static final String avatar = "avatar";
    private static final String signature = "signature";
    private static final String level = "level";
    private static final String privilege = "privilege";
    private static final String status = "status";
    private static final String setting = "setting";
    private static final String createTime = "createTime";
    private static final String lmTime = "lmTime";

    UserServiceImpl(){
        entityToTable.put(id,"id");
        entityToTable.put(nickname, "user_nickname");
        entityToTable.put(avatar, "user_avatar");
        entityToTable.put(signature, "user_signature");
        entityToTable.put(level, "user_level");
        entityToTable.put(privilege, "user_privilege");
        entityToTable.put(status, "user_status");
        entityToTable.put(setting, "user_setting");
        entityToTable.put(createTime, "create_time");
        entityToTable.put(lmTime, "lm_time");
    }

    /**
     * 查看自己的个人空间
     * @param id
     * @return
     */
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

    /**
     * 修改个人信息，个人空间设置
     * @param id
     * @param fieldName
     * @param newValue
     * @return
     */
    @Override
    public ReturnData updateUserInfo(long id, String fieldName, String newValue){
        //字段被人为修改过， 不给更新
        if(!entityToTable.containsKey(fieldName)){
            return ReturnData.error(CodeEnum.USER_UPDATE_EXCEPTION.getCode(), CodeEnum.USER_UPDATE_EXCEPTION.getMessage());
        }
        //检查字段是否有权限更新，值是否合法
        if(!checkValue(fieldName, newValue)){
            return ReturnData.error(CodeEnum.USER_UPDATE_EXCEPTION.getCode(), CodeEnum.USER_UPDATE_EXCEPTION.getMessage());
        }
        //不存在这个用户
        if(userMapper.checkExist(id)!=1){
            return ReturnData.error(CodeEnum.USER_UPDATE_EXCEPTION.getCode(), CodeEnum.USER_UPDATE_EXCEPTION.getMessage());
        }
        //更新用户信息
        userMapper.updateUser(id, entityToTable.get(fieldName), newValue, new Date());
        return  ReturnData.ok();
    }
    private static boolean checkValue(String fieldName, String value){
        if(value == null)
            return false;
        if(fieldName.equals(nickname)){
            if(value!=null && value.length()<255){
                return Pattern.matches("\\*", value);
            }
            return false;
        }else if(fieldName.equals(signature)){
            if(value!=null && value.length()<255){
                return Pattern.matches("\\*", value);
            }
            return false;
        }else if(value.equals(level) && value.length()<=3){
            return value.matches("[0-9]+");
        }else if(value.equals(privilege) && value.length()==1){
            return value.matches("\\[0-3\\]");
        }else if(value.equals(status) && value.length()==1){
            return value.matches("[0-3]");
        }else if(value.equals(setting) && value.length()<=64){
            return value.matches("[0-1]+");
        }
        return false;
    }
}
