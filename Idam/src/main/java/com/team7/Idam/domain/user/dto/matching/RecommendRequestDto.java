package com.team7.Idam.domain.user.dto.matching;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendRequestDto {
    private Long categoryId; // ai 요청 -> 도메인
    private List<String> tags; // ai 요청 -> 기업 태그
}
