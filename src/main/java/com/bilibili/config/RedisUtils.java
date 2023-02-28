package com.bilibili.config;

import com.bilibili.common.utils.BloomFilterHelper;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

@Slf4j
@Component
public class RedisUtils  {
    @Autowired
    @Qualifier(value = "myRedisTemplate")
    private RedisTemplate redisTemplate;



    public RedisTemplate getTemplate(){
        return this.redisTemplate;
    }

    private final String DEFAULT_KEY_PREFIX = "";
    /**
     * 是否存在key
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 返回 key 的剩余的过期时间
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 设置过期时间
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    public boolean exists(String key){
        return this.redisTemplate.hasKey(key);
    }
    public long incrBy(String key){
        return this.redisTemplate.opsForValue().increment(key, 1);
    }
    /**
     * 数据缓存至redis
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public boolean setNX(String key, Object value){
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    public boolean sAdd(String key, Object value){
        try {
            redisTemplate.opsForSet().add(key, value);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return true;
    }
    public boolean sIsMember(String key, Object member){
        return redisTemplate.opsForSet().isMember(key, member);
    }
    /**
     * 从redis中获取缓存数据，转成对象
     *
     * @param key   must not be {@literal null}.
     *
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }

    public <K,V> void zadd(K key, V value, double score){
        try {
            if (key!=null && value != null) {
                redisTemplate.opsForZSet().incrementScore(key, value, score);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("数据缓存至redis失败");
        }
    }
    public void unionAndStore(String key, List<String> otherKeys, String newKey){
        try {
            this.redisTemplate.opsForZSet().unionAndStore(key, otherKeys, newKey);
        }catch (RuntimeException e){
            log.error(e.getMessage());
        }
    }

    public List rangeWithScore(String key){
        Set res= null;
        try {
            res = this.redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
        }catch (RuntimeException e){
        }
        return new ArrayList(res);
    }
    public long hIncr(String key, Object hashKey){
        return redisTemplate.opsForHash().increment(key, hashKey, 1);
    }
    public Object hGet(String key, Object field){
        return redisTemplate.opsForHash().get(key, field);
    }
    public void hSet(String key, Object field, Object value){
        redisTemplate.opsForHash().put(key, field, value);
    }
    public boolean hExists(String key, Object field){
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    public Map hGetAll(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 根据给定的布隆过滤器添加值
     */
    public <T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            System.out.println("key : " + key + " " + "value : " + i);
            redisTemplate.opsForValue().setBit(key, i, true);
        }
    }

    /**
     * 根据给定的布隆过滤器判断值是否存在
     */
    public <T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            System.out.println("key : " + key + " " + "value : " + i);
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }

        return true;
    }

}
