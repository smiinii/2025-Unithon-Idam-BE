package com.team7.Idam.jwt;

import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("🔥 요청 URI: " + uri);
        System.out.println("🔥 들어온 Authorization 헤더: " + request.getHeader("Authorization"));

        // ✅ WebSocket 요청은 필터에서 제외
        if (uri.startsWith("/ws/") || uri.startsWith("/info") || uri.startsWith("/sockjs-node") || uri.startsWith("/ws/chat/info")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 로그인/회원가입/리프레시 요청은 제외
        if (uri.startsWith("/api/refresh") || uri.startsWith("/api/login") || uri.startsWith("/api/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        System.out.println("🔥 추출된 Bearer 토큰: " + token);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            Claims claims = jwtTokenProvider.getClaims(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            List<String> roles = claims.get("roles", List.class);
            System.out.println("🔥 JWT 안 roles: " + roles);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(customUserDetails, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            System.out.println("🔥 SecurityContext에 세팅된 인증 객체: " + authentication);
        } else {
            System.out.println("❌ JWT 유효성 검증 실패 or 토큰 없음");
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // ✅ WebSocket fallback 대응 - 쿼리 파라미터에서 token 추출
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isBlank()) {
            return tokenParam;
        }

        return null;
    }
}
