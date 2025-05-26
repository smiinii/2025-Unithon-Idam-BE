package com.team7.Idam.domain.task.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class AiTagRequestDto {
    private String domain;
    private String prompt;
}
