package com.wxl.shiro.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@Slf4j
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.application.name:}-")
    private String prefix;

    private static final long EXPIRE_TIME = 1800;

    private static final long FLUSH_TIME =1800;



    /**
     * set key-val 键值对
     *
     * @param strKey
     * @param value
     * @param time   过期时间单位秒
     */
    public void setVal(String strKey, Object value, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        String key = getKey(strKey);
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis insert error", e);
            throw e;
        }
    }


    /**
     *  获取一个 key 下的所有键值对 ---> hgetall
     * @param key
     * @return
     */
    public Map<Object, Object> getHashStringAll(String key){
        return redisTemplate.opsForHash().entries(getKey(key));
    }

    public void flushKey(String key){
        try {
            String flushKey = getKey(key);
            redisTemplate.expire(flushKey, FLUSH_TIME, TimeUnit.SECONDS);
        }catch (Exception e) {
            log.error("redis flush error", e);
            throw e;
        }
    }

    public void setHashVal(String hashKey, String valKey, String val, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        String key = getKey(hashKey);
        try {
            redisTemplate.opsForHash().put(key, valKey, val);
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }catch (Exception e) {
            log.error("redis insert error", e);
            throw e;
        }
    }

    public void setHashVal(String hashKey, String valKey, Object value, long time) {
        if (time <= 0) {
            time = EXPIRE_TIME;
        }
        String key = getKey(hashKey);
        try {
            this.redisTemplate.opsForHash().put(key, valKey, value);
            redisTemplate.expire(key, time, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("@@@@@@@@@@ --> set " + key + " value error, cause of "  , e);
            throw e;
        }
    }

    /**
     * 该方法返回值可能带引号
     * @param hashKey
     * @param valKey
     * @return
     */
    public Object getHashStringVal(String hashKey, String valKey){
        String key = getKey(hashKey);
        return stringRedisTemplate.opsForHash().get(key, valKey);
    }

    public Object getHashVal(String hashKey,String valKey){
        String key = getKey(hashKey);
        return redisTemplate.opsForHash().get(key, valKey);
    }


    public String getVal(String strKey) {
        String key = getKey(strKey);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public Object getObjVal(String strKey) {
        String key = getKey(strKey);
        return redisTemplate.opsForValue().get(key);
    }


    /**
     * 获取失效时间
     * @param strKey
     * @return
     */
    public Long getExpireTime(String strKey){
        String key = getKey(strKey);
        return stringRedisTemplate.getExpire(key);
    }

    public long delHashVal(String key,String hashKey){
        return redisTemplate.opsForHash().delete(getKey(key),hashKey);
    }

    public boolean delVal(String key){
        return redisTemplate.delete(getKey(key));
    }

    /**
     * 获取key下所有的hash
     * @param key
     */
    public List<Object> getHashValueAll(String key){
        return redisTemplate.opsForHash().values(getKey(key));
    }



    private String getKey(String key) {
        return prefix + key;
    }
}