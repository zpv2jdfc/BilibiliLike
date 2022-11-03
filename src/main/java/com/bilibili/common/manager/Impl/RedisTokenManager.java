package com.bilibili.common.manager.Impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.bilibili.common.constant.BaseConstant;
import com.bilibili.common.manager.TokenManager;
import com.bilibili.common.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenManager implements TokenManager {
    private RedisTemplate<Long, String> redis;

    @Autowired
    @Qualifier(value = "redisTemplate")
    public void setRedis(RedisTemplate redis) {
        this.redis = redis;
        //泛型设置成Long后必须更改对应的序列化方案
        redis.setKeySerializer(new JdkSerializationRedisSerializer());
    }
    public void add(long id, String token){
        redis.opsForValue().set(id, token);
    }
    @Override
    public String createToken(long userId) {
        String id = String.valueOf(userId);
        Map<String,String> map =new HashMap();
        map.put("id",id);
        return JWTUtil.getToken(map);
    }

    @Override
    public boolean checkToken(String token) {
        if (token == null) {
            return false;
        }
        long id = -1;
        try {
            getId(token);
        }catch (Exception e){
            return false;
        }
        String redisToken = redis.boundValueOps(id).get();
        if (redisToken == null || !redisToken.equals(token)) {
            return false;
        }
        //验证成功，延长token有效时间
        redis.boundValueOps(id).expire(BaseConstant.TOKEN_EXPIRES_DAY, TimeUnit.DAYS);
        return true;
    }

    @Override
    public long getId(String token) {
        DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
        long id =decodedJWT.getClaim("id").asLong();
        return id;
    }

    @Override
    public void deleteToken(long userId) {
        redis.delete(userId);
    }

    @Override
    public Date getExpiresDay(String token) {
        DecodedJWT decodedJWT = JWTUtil.getTokenInfo(token);
        return decodedJWT.getExpiresAt();
    }
}
