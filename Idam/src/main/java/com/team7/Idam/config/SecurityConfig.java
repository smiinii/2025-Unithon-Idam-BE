package com.team7.Idam.config;

import com.team7.Idam.jwt.JwtRefreshAuthenticationFilter;
import com.team7.Idam.jwt.JwtTokenProvider;
import com.team7.Idam.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    // BCrypt >> 비밀번호를 "단방향 해시"로 안전하게 변환해주는 암호화 알고리즘
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // accessToken 검증 필터 (refresh, logout 경로는 검증 스킵하도록 내부에 작성함)
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        // refreshToken 검증 필터 (refresh, logout 경로만 검증하도록 내부에 작성함)
        JwtRefreshAuthenticationFilter jwtRefreshAuthenticationFilter = new JwtRefreshAuthenticationFilter(jwtTokenProvider);

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup/**", "/api/login/**").permitAll()
                        .requestMatchers("/api/refresh", "/api/logout").permitAll()
                        .anyRequest().authenticated()
                )
                // refreshToken 검증 필터 → accessToken 검증 필터보다 먼저 동작
                .addFilterBefore(jwtRefreshAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // accessToken 검증 필터 (refresh, logout 경로는 이 필터 안에서 스킵)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // AuthenticationManager Bean 등록
    /*
        유효한 사용자인지(로그인 성공인지 실패인지) 검증.
        Spring Security 내부에 이미 설정된 인증 로직을 빈으로 등록.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}