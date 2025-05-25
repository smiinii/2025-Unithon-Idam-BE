package com.team7.Idam.global.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenStore {

    private final RedisTemplate<String, String> redisTemplate;
    private final long refreshTokenValidity = 7 * 24 * 60 * 60; // 7일 (초 단위)

    public RefreshTokenStore(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 저장 (디바이스별 저장, TTL 설정)
    public void save(Long userId, String deviceId, String refreshToken) {
        String key = buildKey(userId, deviceId);
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenValidity, TimeUnit.SECONDS);
    }

    // 조회
    public String get(Long userId, String deviceId) {
        String key = buildKey(userId, deviceId);
        return redisTemplate.opsForValue().get(key);
    }

    // 삭제
    public boolean delete(Long userId, String deviceId) {
        String key = buildKey(userId, deviceId);
        Boolean existed = redisTemplate.hasKey(key);
        redisTemplate.delete(key);
        return Boolean.TRUE.equals(existed);
    }

    // key 생성
    private String buildKey(Long userId, String deviceId) {
        return "refreshToken:" + userId + ":" + deviceId;
    }
}
