package com.team7.Idam.config;

import com.team7.Idam.domain.user.repository.UserRepository;
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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // BCrypt >> 비밀번호를 "단방향 해시"로 안전하게 변환해주는 암호화 알고리즘
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // accessToken 검증 필터 (refresh, logout 경로는 검증 스킵하도록 내부에 작성함)
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
        // refreshToken 검증 필터 (refresh, logout 경로만 검증하도록 내부에 작성함)
        JwtRefreshAuthenticationFilter jwtRefreshAuthenticationFilter = new JwtRefreshAuthenticationFilter(jwtTokenProvider);

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/signup/**",
                                "/api/login",
                                "/api/refresh",
                                "/api/ai-tag",
                                "/api/categories/**",
                                "/ws/**",
                                "/info",
                                "/ws/chat/info",
                                "/sockjs-node/**"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("🔥 인증 실패 (401 Unauthorized): {}", authException.getMessage());
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json; charset=UTF-8");
                            response.getWriter().write("{\"error\": \"인증 실패: " + authException.getMessage() + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("🔥 권한 부족 (403 Forbidden): {}", accessDeniedException.getMessage());
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json; charset=UTF-8");
                            response.getWriter().write("{\"error\": \"권한 부족: " + accessDeniedException.getMessage() + "\"}");
                        })
                )
                .addFilterBefore(jwtRefreshAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/ws/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
