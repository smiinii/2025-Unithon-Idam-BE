package com.team7.Idam.domain.user.dto.matching;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ScoredStudentResponseDto {
    private Long userId;
    private String nickname;
    private String profileImage;
    private int score;
}
