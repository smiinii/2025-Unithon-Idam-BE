package com.team7.Idam.config;

import com.team7.Idam.jwt.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    // 1. 메시지 브로커 설정 (구독/sub, 발행/pub 구분)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 주소 (ex: /sub/chat/room/1)
        registry.enableSimpleBroker("/sub");
        // 클라이언트가 보낼 주소 (ex: /pub/chat/send)
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // 2. WebSocket 연결 endpoint 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 실제 클라이언트가 연결할 WebSocket 주소
        registry.addEndpoint("/ws")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns("*") // CORS 허용 (프론트와 포트 다르면 필수)
                .withSockJS(); // SockJS: WebSocket을 지원하지 않는 브라우저 대안
    }
}
