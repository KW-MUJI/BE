package com.muji_backend.kw_muji.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Service
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    public String getData(String key) {
        try{
            ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
            return valueOperations.get(key);
        } catch(Exception e){
            return "redis get 문제 발생";
        }
    }

    public void setDataExpire(String key,String value,long duration) {
        try{
            ValueOperations<String,String> valueOperations=redisTemplate.opsForValue();
            Duration expireDuration=Duration.ofSeconds(duration);
            valueOperations.set(key,value,expireDuration);
        } catch(Exception e) {
            log.warn("redis set 문제 발생:{}", e);
        }
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
