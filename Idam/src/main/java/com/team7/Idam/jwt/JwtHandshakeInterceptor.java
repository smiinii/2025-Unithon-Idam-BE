package com.team7.Idam.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String uri = request.getURI().toString();
        String token = null;

        // 쿼리 파라미터에서 token 추출
        if (uri.contains("token=")) {
            token = uri.substring(uri.indexOf("token=") + 6);
            int ampIndex = token.indexOf('&'); // 여러 쿼리 파라미터 대비
            if (ampIndex != -1) {
                token = token.substring(0, ampIndex);
            }
        }

        // 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            attributes.put("userId", userId); // WebSocket 세션에 저장
            System.out.println("✅ WebSocket 인증 성공, userId: " + userId);
            return true;
        }

        System.out.println("❌ WebSocket 인증 실패: JWT 누락 또는 유효하지 않음");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 생략 가능
    }
}
