package com.team7.Idam.domain.task.controller;

import com.team7.Idam.domain.task.dto.AiTagRequestDto;
import com.team7.Idam.domain.task.dto.AiTagResponseDto;
import com.team7.Idam.domain.task.service.AiTagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/api")
public class AiTagController {

    private static final Logger log = LoggerFactory.getLogger(AiTagController.class);

    private final AiTagService aiTagService;

    public AiTagController(AiTagService aiTagService) {
        this.aiTagService = aiTagService;
    }

    @PostMapping("/ai-tag")
    public Mono<ResponseEntity<AiTagResponseDto>> postAiTag(@RequestBody AiTagRequestDto requestDto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔥 컨트롤러 내 현재 Authentication: " + auth);
        if (auth != null) {
            System.out.println("🔥 현재 권한들: " + auth.getAuthorities());
        }

        return aiTagService.fetchDeduplicatedTagList(requestDto)
                .map(deduplicatedTags -> {
                    AiTagResponseDto responseDto = AiTagResponseDto.builder()
                            .tag(deduplicatedTags)
                            .build();

                    return ResponseEntity.ok()
                            .header("Content-Type", "application/json; charset=UTF-8")
                            .body(responseDto);
                })
                .onErrorResume(e -> {
                    log.error("🔥 컨트롤러 처리 중 에러 발생", e);
                    AiTagResponseDto errorResponse = AiTagResponseDto.builder()
                            .tag(List.of("에러 발생: " + e.getMessage()))
                            .build();
                    return Mono.just(ResponseEntity.internalServerError().body(errorResponse));
                });
    }
}
