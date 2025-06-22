package com.team7.Idam.domain.user.dto.profile.student;

import lombok.Getter;

import java.util.List;

@Getter
public class UpdateTagsRequestDto {
    private List<String> tags;
}
