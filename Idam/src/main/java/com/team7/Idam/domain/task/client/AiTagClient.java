package com.team7.Idam.domain.task.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class AiTagClient {
    private static final Logger log = LoggerFactory.getLogger(AiTagClient.class);

    private final WebClient webClient;

    public AiTagClient() {
        this.webClient = WebClient.builder()
                .baseUrl("https://d65b-117-16-195-75.ngrok-free.app") // 정확한 ngrok 주소
                .build();
    }

    public Mono<String> getAiTag(String domain, String prompt) {
        return webClient.post()
                .uri("/api/tag")
                .header("Content-Type", "application/json; charset=UTF-8")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("domain", domain, "prompt", prompt))
                .retrieve()
                .bodyToMono(byte[].class)  // 🔥 바이트로 수신
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))  // 🔥 직접 UTF-8로 디코딩
                .doOnSubscribe(sub -> log.info("🔥 WebClient 요청 준비됨"))
                .doOnNext(raw -> log.info("🔥 수신된 Raw JSON: {}", raw));
    }
}
