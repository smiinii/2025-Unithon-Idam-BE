package com.team7.Idam.domain.user.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
    [Controller → 클라이언트 응답] 할 때 사용. accessToken + userType + userId만 응답.
    (refreshToken은 응답하지 않고, 쿠키에만 저장)
 */
@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String userType;
    private Long userId;
}
