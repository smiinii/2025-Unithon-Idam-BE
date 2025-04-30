package com.team7.Idam.domain.user.dto.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
    [Service → Controller] 로 넘길 때 내부적으로 사용.
    accessToken + refreshToken + userType 모두 가진다.
 */
@Getter
@AllArgsConstructor
public class LoginResultDto {
    private String accessToken;
    private String refreshToken;
    private String userType;
}
