package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.signup.StudentSignupRequestDto;
import com.team7.Idam.domain.user.dto.signup.CompanySignupRequestDto;
import com.team7.Idam.domain.user.dto.login.LoginRequestDto;
import com.team7.Idam.domain.user.dto.login.LoginResponseDto;
import com.team7.Idam.domain.user.dto.login.LoginResultDto;
import com.team7.Idam.domain.user.service.AuthService;
import com.team7.Idam.global.util.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenStore refreshTokenStore;

    // 학생 회원가입
    @PostMapping("/api/signup/student")
    public ResponseEntity<String> signupStudent(@Valid @RequestBody StudentSignupRequestDto request) {
        authService.signupStudent(request);
        return ResponseEntity.ok("학생 회원가입이 완료되었습니다.");
    }

    // 기업 회원가입
    @PostMapping("/api/signup/company")
    public ResponseEntity<String> signupCompany(@Valid @RequestBody CompanySignupRequestDto request) {
        authService.signupCompany(request);
        return ResponseEntity.ok("기업 회원가입이 완료되었습니다.");
    }

    // 학생 로그인
    @PostMapping("/api/login/student")
    public ResponseEntity<LoginResponseDto> loginStudent(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResultDto loginResult = authService.loginStudent(request);
        addRefreshTokenToCookie(response, loginResult.getRefreshToken());
        return ResponseEntity.ok(new LoginResponseDto(loginResult.getAccessToken(), loginResult.getUserType()));
    }

    // 기업 로그인
    @PostMapping("/api/login/company")
    public ResponseEntity<LoginResponseDto> loginCompany(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResultDto loginResult = authService.loginCompany(request);
        addRefreshTokenToCookie(response, loginResult.getRefreshToken());
        return ResponseEntity.ok(new LoginResponseDto(loginResult.getAccessToken(), loginResult.getUserType()));
    }

    // Refresh Token으로 Access Token 재발급
    @PostMapping("/api/refresh")
    public ResponseEntity<LoginResponseDto> reissueToken(@RequestParam Long userId, HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에 저장된 refreshToken을 사용하여 재발급 후, 다시 쿠키에 저장.
        String refreshToken = extractRefreshTokenFromCookie(request);
        LoginResultDto newTokens = authService.reissueToken(userId, refreshToken);
        addRefreshTokenToCookie(response, newTokens.getRefreshToken());
        return ResponseEntity.ok(new LoginResponseDto(newTokens.getAccessToken(), newTokens.getUserType()));
    }

    // 로그아웃
    @PostMapping("/api/logout")
    public ResponseEntity<String> logout(@RequestParam Long userId, HttpServletResponse response) {
        refreshTokenStore.delete(userId); // refreshToken 삭제

        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(expiredCookie);

        return ResponseEntity.ok("로그아웃이 완료되었습니다.");
    }

    // Refresh Token을 쿠키에 저장 (로그인, 재발급 시 사용)
    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);          // 자바스크립트 접근 금지
        cookie.setSecure(true);             // HTTPS에서만 전송
        cookie.setPath("/");                // 모든 경로에서 사용
        cookie.setMaxAge(60 * 60 * 24 * 7);  // 7일 (단위: 초)
        response.addCookie(cookie);
    }

    // 쿠키에서 Refresh Token 꺼내기 (재발급 시 사용)
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new IllegalArgumentException("Refresh Token 쿠키가 존재하지 않습니다.");
    }
}
