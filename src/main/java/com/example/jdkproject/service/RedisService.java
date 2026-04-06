package com.example.jdkproject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public Object getFromRedis(String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public List<String> getListFromRedis(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    public void saveToRedis(String key, String data, long ttl) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, data, ttl, TimeUnit.MILLISECONDS);
    }

    public void saveListToRedis(String key, String data, long ttl) {
        redisTemplate.opsForList().leftPush(key, data);
        redisTemplate.opsForList().trim(key, 0, 4);
        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS);
    }

    public void deleteFromRedis(String key) {
        redisTemplate.delete(key);
    }
}
