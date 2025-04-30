package com.team7.Idam.global.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class RefreshTokenStore {

    private final Map<Long, String> refreshTokenStore = new ConcurrentHashMap<>();

    // 유저 아이디와 refreshToken을 해시 형태(key, value)로 저장.
    public void save(Long userId, String refreshToken) {
        refreshTokenStore.put(userId, refreshToken);
    }

    public String get(Long userId) {
        return refreshTokenStore.get(userId);
    }

    public void delete(Long userId) {
        refreshTokenStore.remove(userId);
    }
}
