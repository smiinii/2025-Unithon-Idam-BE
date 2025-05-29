package com.team7.Idam.domain.user.dto.matching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScoredStudentResponseDto {
    private Long userId;
    private String name;
    private String profileImage;
    private int score;
}
