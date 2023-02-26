package com.bilibili.common.utils;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.bilibili.common.constant.BaseConstant;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class JWTUtil {
    private static final String SING="888888";

    //生成令牌
    public static String getToken(Map<String,String> map){
        Map<String, Object> headerMap = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");
        //获取日历对象
        Calendar calendar=Calendar.getInstance();
        //默认7天过期
        calendar.add(Calendar.DATE, BaseConstant.TOKEN_EXPIRES_DAY);
        //新建一个JWT的Builder对象
        JWTCreator.Builder builder = JWT.create();
        builder.withHeader(headerMap);
        //将map集合中的数据设置进payload
        map.forEach((k,v)->{
            builder.withClaim(k, v);
        });
        //设置过期时间和签名
        String token = builder.withExpiresAt(calendar.getTime()).sign(Algorithm.HMAC256(SING));
        return token;
    }
    public static Map<String, Claim> verifyToken(String token) throws Exception {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SING)).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            // e.printStackTrace();
            // token 校验失败, 抛出Token验证非法异常
            throw e;
        }
        return jwt.getClaims();
    }
    public static Long getUID(String token){
        Map<String, Claim> claims ;
        try{
            claims = verifyToken(token);
        }catch (Exception e){
            return -1l;
        }
        Claim user_id_claim = claims.get("id");
        if (null == user_id_claim || StringUtils.isEmpty(user_id_claim.asString())) {
            return -1l;
        }
        return Long.valueOf(user_id_claim.asString());
    }

    public  static DecodedJWT getTokenInfo(String token){
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }
}
