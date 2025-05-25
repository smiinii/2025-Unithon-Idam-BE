package com.team7.Idam.config;

import com.team7.Idam.domain.user.entity.TagCategory;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.repository.TagCategoryRepository;
import com.team7.Idam.domain.user.repository.TagOptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // 유저 초기화 전에 실행되도록
@RequiredArgsConstructor
@Transactional
public class TagDataInitializer implements CommandLineRunner {

    private final TagCategoryRepository tagCategoryRepository;
    private final TagOptionRepository tagOptionRepository;

    @Override
    public void run(String... args) {
        if (tagCategoryRepository.count() > 0 || tagOptionRepository.count() > 0) {
            System.out.println("태그 데이터가 이미 존재하여 초기화를 건너뜁니다.");
            return;
        }

        // 1. 카테고리 생성
        TagCategory it = tagCategoryRepository.save(new TagCategory("IT·프로그래밍"));
        TagCategory design = tagCategoryRepository.save(new TagCategory("디자인"));
        TagCategory marketing = tagCategoryRepository.save(new TagCategory("마케팅"));

        // 2. 태그 생성
        tagOptionRepository.save(new TagOption("Java", it));
        tagOptionRepository.save(new TagOption("Spring", it));
        tagOptionRepository.save(new TagOption("React", it));
        tagOptionRepository.save(new TagOption("Typescript", it));
        tagOptionRepository.save(new TagOption("SQL", it));

        tagOptionRepository.save(new TagOption("Adobe Photoshop", design));
        tagOptionRepository.save(new TagOption("Adobe Illustrator", design));
        tagOptionRepository.save(new TagOption("Figma", design));

        tagOptionRepository.save(new TagOption("ShoppingMall Operation", marketing));
        tagOptionRepository.save(new TagOption("SNS Management", marketing));
        tagOptionRepository.save(new TagOption("CPA Marketing", marketing));

        System.out.println("태그 카테고리 및 옵션 초기화 완료.");
    }
}
