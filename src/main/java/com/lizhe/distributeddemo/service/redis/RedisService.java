package com.lizhe.distributeddemo.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis服务类
 */
@Component
public class RedisService {
    /**
     * 默认过期时长，单位：秒
     */
    public static final long DEFAULT_EXPIRE = 60 * 60 * 24;

    /**
     * 不设置过期时长
     */
    public static final long NOT_EXPIRE = -1;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    public boolean existsKey(String key) {
        return redisTemplate.hasKey(key);
    }

}
