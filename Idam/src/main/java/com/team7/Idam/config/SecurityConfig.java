package com.team7.Idam.config;

import com.team7.Idam.domain.user.repository.UserRepository;
import com.team7.Idam.jwt.JwtRefreshAuthenticationFilter;
import com.team7.Idam.jwt.JwtTokenProvider;
import com.team7.Idam.jwt.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, userRepository);
        JwtRefreshAuthenticationFilter jwtRefreshAuthenticationFilter = new JwtRefreshAuthenticationFilter(jwtTokenProvider);

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/api/signup/**",
                                "/api/login",
                                "/api/refresh",
                                "/api/ai-tag",
                                "/api/categories/**",
                                "/api/matching/by-ai",
                                "/api/students/preview",
                                "/api/company/preview",
                                "/ws/**",
                                "/ws/chat/**",
                                "/sockjs-node/**",
                                "/info"
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

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // ✅ API 요청용
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000", "https://idam.vercel.app")
                        .allowedMethods("*")
                        .allowedHeaders("Content-Type", "Authorization", "Cookie") // 🔥 쿠키 추가
                        .allowCredentials(true);

                // ✅ 웹소켓용 (STOMP + SockJS 핸드셰이크 포함)
                registry.addMapping("/ws/**")
                        .allowedOrigins("http://localhost:3000", "https://idam.vercel.app")
                        .allowedMethods("*")
                        .allowedHeaders("Content-Type", "Authorization", "Cookie")
                        .allowCredentials(true);

                registry.addMapping("/ws/chat/**")
                        .allowedOrigins("http://localhost:3000", "https://idam.vercel.app")
                        .allowedMethods("*")
                        .allowedHeaders("Content-Type", "Authorization", "Cookie")
                        .allowCredentials(true);
            }
        };
    }
}
