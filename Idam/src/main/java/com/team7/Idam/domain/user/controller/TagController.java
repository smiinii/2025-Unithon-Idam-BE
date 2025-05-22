package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.Tag.TagOptionResponseDto;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.repository.TagOptionRepository;
import com.team7.Idam.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class TagController {

    private final TagOptionRepository tagOptionRepository;

    // 카테고리별 태그 조회
    @GetMapping("/{categoryId}/tags")
    public ResponseEntity<ApiResponse<List<TagOptionResponseDto>>> getTagsByCategory(@PathVariable Long categoryId) {
        List<TagOption> tags = tagOptionRepository.findAllByCategoryId(categoryId);
        List<TagOptionResponseDto> response = tags.stream()
                .map(TagOptionResponseDto::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success("태그 조회 성공", response));
    }
}
