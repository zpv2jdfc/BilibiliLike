package com.bilibili.service.Impl;

import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.dao.OAuthMapper;
import com.bilibili.dao.UserMapper;
import com.bilibili.entity.UserEntity;
import com.bilibili.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OAuthMapper oAuthMapper;

    private enum LogType{
        PASSWORD("PASSWORD"),
        WX("WX"),
        QQ("QQ");
        public String getCode(){
            return code;
        }
        private final String code;
        LogType(String s){
            this.code = s;
        }
    }

    private static final String regEx =  "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
    @Override
    public ReturnData createUser(String name, String identitytype, String identifier, String credential){
        if(!checkRegistName(name) || !checkIdentify(identitytype, identifier, credential)){
            return ReturnData.error(CodeEnum.USER_NAME_ILLEGAL.getCode(), CodeEnum.USER_NAME_ILLEGAL.getMessage());
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setNickName(name);
        userMapper.createUser(userEntity);
        long id = userEntity.getId();
        if(identitytype.equals(LogType.PASSWORD.getCode()))
            oAuthMapper.createUser(id, identitytype, LogType.PASSWORD.getCode(), credential);
        else
            oAuthMapper.createUser(id, identitytype, identifier, credential);
        Map data = new HashMap();
        data.put("id", id);
        ReturnData ret = ReturnData.ok();
        ret.setData(data);
        return ret;
    }
    private static boolean checkIdentify(String identitytype, String identifier, String credential){
        if(identitytype==null || credential==null)
            return false;
        if(identitytype.equals(LogType.PASSWORD.getCode())){
            if(credential.length()<5 || credential.length()>=256)
                return false;
            return true;
        }else if(identitytype.equals(LogType.QQ.getCode())){
            if(identifier==null)
                return false;
            return true;
        }else if(identitytype.equals(LogType.WX.getCode())){
            if(identifier==null)
                return false;
            return true;
        }
        return false;
    }
    private static boolean checkRegistName(String name){
        if(name==null || name.length()<=3 || name.length()>=256)
            return false;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(name);
        return !m.find();
    }
}
