package com.bilibili.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ReturnData extends HashMap<String,Object> {
    private static final long serialVersionUID = 1L;
    public Integer getCode() {
        return (Integer) this.get("code");
    }


    public ReturnData setData(Object data) {
        put("data",data);
        return this;
    }
    public <T> T getData(TypeReference<T>reference){
        return getData("data", reference);
    }
    public <T> T getData(String key, TypeReference<T> reference){
        Object value = get(key);
        String jsonString = JSON.toJSONString(value);
        T t = JSON.parseObject(jsonString, reference);
        return t;
    }
    public static ReturnData error() {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "未知异常，请联系管理员");
    }

    public static ReturnData error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    public static ReturnData error(int code, String msg) {
        return ok(code, msg);
    }

    public static ReturnData ok(String msg) {
        ReturnData r = new ReturnData();
        r.put("msg", msg);
        return r;
    }

    public static ReturnData ok(int code, String msg) {
        ReturnData r = new ReturnData();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static ReturnData ok() {
        return new ReturnData();
    }
}
