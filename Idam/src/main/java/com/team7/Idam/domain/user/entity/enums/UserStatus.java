package com.team7.Idam.domain.user.entity.enums;

public enum UserStatus {
    ACTIVE,           // 정상
    WARNING_1,        // 경고 1회
    WARNING_2,        // 경고 2회
    WARNING_3,        // 경고 3회
    SUSPEND_1,        // 1일 정지
    SUSPEND_3,        // 3일 정지
    SUSPEND_7,        // 7일 정지
    PERMANENT_BAN     // 영구 정지
}