package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.matching.RecommendRequestDto;
import com.team7.Idam.domain.user.dto.matching.ScoredStudentResponseDto;
import com.team7.Idam.domain.user.service.MatchingService;
import com.team7.Idam.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/api/matching")
    public ResponseEntity<ApiResponse<List<ScoredStudentResponseDto>>> recommend(@RequestBody RecommendRequestDto request) {
        List<ScoredStudentResponseDto> recommended = matchingService.recommendStudentsByCategory(request);
        ApiResponse<List<ScoredStudentResponseDto>> response = ApiResponse.success("매칭 완료", recommended);
        return ResponseEntity.ok(response);
    }
}
