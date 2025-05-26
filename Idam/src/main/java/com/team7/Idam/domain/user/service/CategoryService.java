package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.entity.TagCategory;
import com.team7.Idam.domain.user.repository.TagCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
    Ai 태그 뽑기 & 학생-기업 매칭
    (domain → categoryId 변환용)
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final TagCategoryRepository tagCategoryRepository;

    public Long findCategoryIdByName(String categoryName) {
        return tagCategoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리 이름을 찾을 수 없습니다."))
                .getId();
    }
}