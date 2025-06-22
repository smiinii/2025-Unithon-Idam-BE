package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.login.*;
import com.team7.Idam.domain.user.dto.signup.StudentSignupRequestDto;
import com.team7.Idam.domain.user.dto.signup.CompanySignupRequestDto;
import com.team7.Idam.domain.user.service.AuthService;
import com.team7.Idam.global.util.RefreshTokenStore;
import com.team7.Idam.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.team7.Idam.global.dto.ApiResponse;

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
    public ResponseEntity<ApiResponse> signupStudent(@Valid @RequestBody StudentSignupRequestDto request) {
        authService.signupStudent(request);
        return ResponseEntity.ok(ApiResponse.success("학생 회원가입이 완료되었습니다."));
    }

    // 기업 회원가입
    @PostMapping("/api/signup/company")
    public ResponseEntity<ApiResponse> signupCompany(@Valid @RequestBody CompanySignupRequestDto request) {
        authService.signupCompany(request);
        return ResponseEntity.ok(ApiResponse.success("기업 회원가입이 완료되었습니다."));
    }

    // 로그인
    @PostMapping("/api/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResultDto loginResult = authService.login(request);
        addRefreshTokenToCookie(response, loginResult.getRefreshToken());

        LoginResponseDto dto = new LoginResponseDto(
                loginResult.getAccessToken(),
                loginResult.getUserType(),
                loginResult.getUserId()
        );

        System.out.println("🔥 LoginResponseDto: " + dto); // 로그 찍기

        return ResponseEntity.ok(dto);
    }

    // 로그아웃
    @PostMapping("/api/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody LogoutRequestDto request,
            HttpServletResponse response) {

        Long userId = userDetails.getId();
        String deviceId = request.getDeviceId();

        boolean deleted = refreshTokenStore.delete(userId, deviceId);

        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        expiredCookie.setPath("/");
        expiredCookie.setMaxAge(0);
        response.addCookie(expiredCookie);

        if (!deleted) {
            return ResponseEntity.ok(ApiResponse.success("이미 로그아웃된 상태입니다."));
        }

        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다."));
    }


    // Refresh Token을 쿠키에 저장 (로그인, 재발급 시 사용)
    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(60 * 60 * 24 * 7)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        System.out.println("🔥 refreshToken 쿠키 설정 완료");
        System.out.println("→ Token: " + refreshToken);
        System.out.println("→ 전체 헤더: " + cookie.toString());
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

    // Refresh Token으로 Access Token 재발급
    @PostMapping("/api/refresh")
    public ResponseEntity<RefreshTokenResponseDto> reissueToken(
            @RequestParam Long userId,
            @RequestParam String deviceId,
            HttpServletRequest request,
            HttpServletResponse response) {

        // 🔍 들어온 요청 로그 확인
        System.out.println("🔥 /api/refresh 요청 도착");
        System.out.println("🔥 Request Method: " + request.getMethod());
        System.out.println("🔥 Request URI: " + request.getRequestURI());
        System.out.println("🔥 userId 파라미터: " + userId);
        System.out.println("🔥 deviceId 파라미터: " + deviceId);

        // 🔍 요청 헤더 전체 출력
        System.out.println("🔥 요청 헤더 목록:");
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            System.out.println("  ↪ " + headerName + ": " + request.getHeader(headerName));
        });

        // 🔍 요청 쿠키 출력
        System.out.println("🔥 요청 쿠키 목록:");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println("  🍪 " + cookie.getName() + "=" + cookie.getValue());
            }
        } else {
            System.out.println("  ❌ 쿠키 없음");
        }

        // 🔧 기존 재발급 로직
        String refreshToken = extractRefreshTokenFromCookie(request);
        LoginResultDto newTokens = authService.reissueToken(userId, deviceId, refreshToken);
        addRefreshTokenToCookie(response, newTokens.getRefreshToken());

        return ResponseEntity.ok(new RefreshTokenResponseDto(newTokens.getAccessToken()));
    }
}

