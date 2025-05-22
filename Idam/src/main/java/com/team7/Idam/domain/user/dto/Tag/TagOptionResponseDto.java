package com.team7.Idam.domain.user.dto.Tag;

import com.team7.Idam.domain.user.entity.TagOption;

public record TagOptionResponseDto(Long id, String tagName) {
    public static TagOptionResponseDto from(TagOption tag) {
        return new TagOptionResponseDto(tag.getTagId(), tag.getTagName());
    }
}
