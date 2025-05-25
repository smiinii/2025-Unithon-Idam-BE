package com.team7.Idam.domain.user.dto.login;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/*
    [Controller → 클라이언트 응답] 할 때 사용. accessToken + userType + userId만 응답.
    (refreshToken은 응답하지 않고, 쿠키에만 저장)
 */
@Getter
@AllArgsConstructor
@ToString // ← 추가
@JsonInclude(JsonInclude.Include.ALWAYS) // null 값 포함해서 응답
public class LoginResponseDto {
    private String accessToken;
    private String userType;
    private Long userId;
}
