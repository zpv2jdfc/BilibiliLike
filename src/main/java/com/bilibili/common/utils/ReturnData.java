package com.bilibili.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.Map;

public class ReturnData extends HashMap<String,Object> {
    private static final long serialVersionUID = 1L;
    public Integer getCode() {
        return (Integer) this.get("code");
    }

    public Map baseInfo(){
        Map<String,Object> map = new HashMap<>();

        return map;
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
}
