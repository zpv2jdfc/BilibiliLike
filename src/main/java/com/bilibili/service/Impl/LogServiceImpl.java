package com.bilibili.service.Impl;

import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.dao.OAuthMapper;
import com.bilibili.dao.UserMapper;
import com.bilibili.entity.OAuthEntity;
import com.bilibili.entity.UserEntity;
import com.bilibili.service.LogService;
import com.bilibili.vo.UserLoginVo;
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
        oAuthMapper.createUser(id, identitytype, identifier, credential);
        Map data = new HashMap();
        data.put("id", id);
        ReturnData ret = ReturnData.ok();
        ret.setData(data);
        return ret;
    }

    @Override
    public ReturnData login(UserLoginVo vo) {
        String identitytype = vo.getIdentitytype();
        String identifier = vo.getIdentifier();
        String credential = vo.getCredential();
        if(!checkIdentify(identitytype, identifier, credential)){
            return ReturnData.error(CodeEnum.USER_NAME_ILLEGAL.getCode(), CodeEnum.USER_NAME_ILLEGAL.getMessage());
        }
        OAuthEntity entity = new OAuthEntity();
        entity.setIdentityType(identitytype);
        entity.setIdentifier(identifier);
        entity.setCredential(credential);
        OAuthEntity oAuthEntity = oAuthMapper.getUserByUserId(entity);
        if(!identifier.equals(oAuthEntity.getIdentifier()) || !credential.equals(oAuthEntity.getCredential())){
            return ReturnData.error(CodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), CodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
        }
        long userid = oAuthEntity.getUserId();
        UserEntity userEntity = userMapper.getUserById(userid);
        Map data = new HashMap();
        data.put("id", userEntity.getId());
        data.put("nickName",userEntity.getNickName());
        data.put("avatar",userEntity.getAvatar());
        data.put("signature",userEntity.getSingature());
        data.put("level",userEntity.getLevel());
        data.put("privilege",userEntity.getPrivilege());
        data.put("status",userEntity.getStatus());
        data.put("setting",userEntity.getSetting());
        return ReturnData.ok().setData(data);
    }

    private static boolean checkIdentify(String identitytype, String identifier, String credential){
        if(identitytype==null || credential==null)
            return false;
//        邮箱登录
        if(identitytype.equals(LogType.PASSWORD.getCode())){
            if(credential.length()<5 || credential.length()>=256 || identifier==null)
                return false;

            return true;
        }else if(identitytype.equals(LogType.QQ.getCode())){ // QQ登录
            if(identifier==null)
                return false;
            return true;
        }else if(identitytype.equals(LogType.WX.getCode())){  // 微信登录
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
