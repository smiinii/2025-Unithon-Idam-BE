package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.task.dto.AiTagRequestDto;
import com.team7.Idam.domain.task.service.AiTagService;
import com.team7.Idam.domain.user.dto.matching.RecommendRequestDto;
import com.team7.Idam.domain.user.dto.matching.ScoredStudentResponseDto;
import com.team7.Idam.domain.user.service.CategoryService;
import com.team7.Idam.domain.user.service.MatchingService;
import com.team7.Idam.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    private final AiTagService aiTagService;
    private final CategoryService categoryService;


    @PostMapping("by-tags")
    public ResponseEntity<ApiResponse<List<ScoredStudentResponseDto>>> recommend(@RequestBody RecommendRequestDto request) {
        List<ScoredStudentResponseDto> recommended = matchingService.recommendStudentsByCategory(request);
        ApiResponse<List<ScoredStudentResponseDto>> response = ApiResponse.success("매칭 완료", recommended);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/by-ai")
    public Mono<ResponseEntity<ApiResponse<List<ScoredStudentResponseDto>>>> matchFromPrompt(
            @RequestBody AiTagRequestDto request
    ) {
        String domain = request.getDomain();
        String prompt = request.getPrompt();

        return aiTagService.fetchDeduplicatedTagList(
                AiTagRequestDto.builder()
                        .domain(domain)
                        .prompt(prompt)
                        .build()
        ).map(tags -> {
            Long categoryId = categoryService.findCategoryIdByName(domain);
            RecommendRequestDto matchRequest = new RecommendRequestDto(categoryId, tags);
            List<ScoredStudentResponseDto> recommended = matchingService.recommendStudentsByCategory(matchRequest);

            return ResponseEntity.ok(ApiResponse.success("AI 태그 기반 매칭 완료", recommended));
        }).onErrorResume(e -> Mono.just(
                ResponseEntity.internalServerError().body(ApiResponse.failure("매칭 실패: " + e.getMessage()))
        ));
    }
}
