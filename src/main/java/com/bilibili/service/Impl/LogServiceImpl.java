package com.bilibili.service.Impl;

import com.bilibili.common.constant.CodeEnum;
import com.bilibili.common.utils.ReturnData;
import com.bilibili.config.RedisUtils;
import com.bilibili.dao.OAuthMapper;
import com.bilibili.dao.UserMapper;
import com.bilibili.entity.OAuthEntity;
import com.bilibili.entity.UserEntity;
import com.bilibili.service.LogService;
import com.bilibili.vo.UserLoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OAuthMapper oAuthMapper;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RedisUtils redisUtils;

    @Value("${spring.mail.from}")
    private String from;
    @Value("${spring.mail.nickname}")
    private String nickname;

    private enum LogType{
        PASSWORD("PASSWORD"),
        WX("WX"),
        EMAIL("EMAIL"),
        QQ("QQ");
        public String getCode(){
            return code;
        }
        private final String code;
        LogType(String s){
            this.code = s;
        }
    }
    private static final String defaultName = "匿名用户";
    private static final String regEx =  "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
    private final static Pattern partern = Pattern.compile("[a-zA-Z0-9]+[\\.]{0,1}[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]+");

    @Override
    public ReturnData createUser(String name, String identitytype, String identifier, String credential){
        name = defaultName;
        if(!checkRegistName(name) || !checkIdentify(identitytype, identifier, credential)){
            return ReturnData.error(CodeEnum.USER_NAME_ILLEGAL.getCode(), CodeEnum.USER_NAME_ILLEGAL.getMessage());
        }
        OAuthEntity entity = new OAuthEntity();
        entity.setIdentityType(identitytype);
        entity.setIdentifier(identifier);
        OAuthEntity oAuthEntity = oAuthMapper.getUserByCredential(entity);
        if(oAuthEntity!=null){
            return ReturnData.error(CodeEnum.USER_EXIST.getCode(), CodeEnum.USER_EXIST.getMessage());
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setNickName(name);
        userMapper.createUser(userEntity);
        long id = userEntity.getId();
        oAuthMapper.createUser(id, identitytype, identifier, credential);
        Map data = new HashMap();
        data.put("id", id);
        data.put("name", userEntity.getNickName());
        data.put("singature", userEntity.getSignature());
        data.put("avatar", userEntity.getAvatar());
        data.put("level", userEntity.getLevel());
        data.put("privilege", userEntity.getPrivilege());
        data.put("status", userEntity.getStatus());
        data.put("setting", userEntity.getSetting());
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
        OAuthEntity oAuthEntity = oAuthMapper.getUserByCredential(entity);
        if(oAuthEntity==null || !credential.equals(oAuthEntity.getCredential())){
            return ReturnData.error(CodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getCode(), CodeEnum.LOGINACCT_PASSWORD_EXCEPTION.getMessage());
        }
        long userid = oAuthEntity.getUserId();
        UserEntity userEntity = userMapper.getUserById(userid);
        Map data = new HashMap();
        data.put("id", userEntity.getId());
        data.put("nickName",userEntity.getNickName());
        data.put("avatar",userEntity.getAvatar());
        data.put("signature",userEntity.getSignature());
        data.put("level",userEntity.getLevel());
        data.put("privilege",userEntity.getPrivilege());
        data.put("status",userEntity.getStatus());
        data.put("setting",userEntity.getSetting());
        return ReturnData.ok().setData(data);
    }

    private static boolean checkIdentify(String identitytype, String identifier, String credential){
        if(identitytype==null || credential==null)
            return false;
//        用户名登录
        if(identitytype.equals(LogType.PASSWORD.getCode())){
            if(credential.length()<3 || credential.length()>=256 || identifier==null)
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

    public boolean sendVerifyMail(String to){
        if(!emailFormat(to))
            return false;
        String verificationCode = createVerificationCode(5);
        this.redisUtils.getTemplate().opsForValue().set("mailRegist:"+to,verificationCode,1800, TimeUnit.SECONDS);
        StringBuilder content = new StringBuilder();
        content.append("您的验证码为： ");
        content.append(verificationCode);
        content.append("\n验证码将在30分钟后失效");
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(nickname+"<"+this.from+">");
        //邮件接收人
        message.setTo(to);
        //邮件主题
        message.setSubject("用户注册验证码");
        //邮件内容
        message.setText(content.toString());
        //发送邮件
        try{
            mailSender.send(message);
        }catch (MailException e){
            return false;
        }
        return true;
    }
    public ReturnData mailRegist(String email, String verificationCode){
        if(!emailFormat(email)){
            return ReturnData.error(20007,"邮箱格式错误");
        }
        Object code = this.redisUtils.getTemplate().opsForValue().get("mailRegist:"+email);
        if(code==null || !verificationCode.equals(code.toString())){
            return ReturnData.error(CodeEnum.LOGINACCT_VERIFACATIONCODE_EXCEPTION.getCode(), CodeEnum.LOGINACCT_VERIFACATIONCODE_EXCEPTION.getMessage());
        }
//        验证通过
        Integer res = this.oAuthMapper.emailUserExists(email);
//        用户未注册则注册用户
        if(res==null){
            UserEntity userEntity = new UserEntity();
            userEntity.setNickName(email);
            userMapper.createUser(userEntity);
            long id = userEntity.getId();
            this.oAuthMapper.createUser(id, LogType.EMAIL.getCode(), email, "default");
        }
        this.redisUtils.getTemplate().delete("mailRegist:"+email);
        UserEntity data = oAuthMapper.getUserInfoByEmail(email);
        ReturnData ret = ReturnData.ok();
        ret.setData(data);
        return ret;
    }

    private String createVerificationCode(int len){
        StringBuilder sb = new StringBuilder();
        Random random = new Random(System.currentTimeMillis());
        for(int i=0;i<len;++i){
            int temp = -1;
            while(!(temp>=48&&temp<=57) && !(temp>=65&&temp<=90) && !(temp>=97&&temp<=122)){
                temp = random.nextInt(123);
            }
            sb.append((char)temp);
        }
        return sb.toString();
    }
    private boolean emailFormat(String email){
        boolean isMatch = partern.matcher(email).matches();
        return isMatch;
    }

}
