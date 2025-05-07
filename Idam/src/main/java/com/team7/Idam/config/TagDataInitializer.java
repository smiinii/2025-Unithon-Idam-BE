package com.team7.Idam.config;

import com.team7.Idam.domain.user.entity.TagCategory;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.entity.Student;
import com.team7.Idam.domain.user.repository.TagCategoryRepository;
import com.team7.Idam.domain.user.repository.TagOptionRepository;
import com.team7.Idam.domain.user.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class TagDataInitializer implements CommandLineRunner {

    private final TagCategoryRepository tagCategoryRepository;
    private final TagOptionRepository tagOptionRepository;
    private final StudentRepository studentRepository;

    @Override
    public void run(String... args) {
        // 1. 카테고리 생성
        TagCategory itCategory = new TagCategory();
        itCategory.setCategoryName("IT·프로그래밍");
        tagCategoryRepository.save(itCategory);

        TagCategory design = new TagCategory();
        design.setCategoryName("디자인");
        tagCategoryRepository.save(design);

        TagCategory marketing = new TagCategory();
        marketing.setCategoryName("마케팅");
        tagCategoryRepository.save(marketing);

        // 2. 태그 생성

        // itCategory Tags
        TagOption javaTag = new TagOption();
        javaTag.setTagName("Java");
        javaTag.setCategory(itCategory);
        tagOptionRepository.save(javaTag);

        TagOption springTag = new TagOption();
        springTag.setTagName("Spring");
        springTag.setCategory(itCategory);
        tagOptionRepository.save(springTag);

        TagOption reactTag = new TagOption();
        reactTag.setTagName("React");
        reactTag.setCategory(itCategory);
        tagOptionRepository.save(reactTag);

        TagOption typescriptTag = new TagOption();
        typescriptTag.setTagName("Typescript");
        typescriptTag.setCategory(itCategory);
        tagOptionRepository.save(typescriptTag);

        TagOption sqlTag = new TagOption();
        sqlTag.setTagName("SQL");
        sqlTag.setCategory(itCategory);
        tagOptionRepository.save(sqlTag);

        // design Tags
        TagOption adobePhotoshopTag = new TagOption();
        adobePhotoshopTag.setTagName("Adobe Photoshop");
        adobePhotoshopTag.setCategory(design);
        tagOptionRepository.save(adobePhotoshopTag);

        TagOption adobeIllustratorTag = new TagOption();
        adobeIllustratorTag.setTagName("Adobe Illustrator");
        adobeIllustratorTag.setCategory(design);
        tagOptionRepository.save(adobeIllustratorTag);

        TagOption figmaTag = new TagOption();
        figmaTag.setTagName("Figma");
        figmaTag.setCategory(design);
        tagOptionRepository.save(figmaTag);

        // marketing Tags
        TagOption shoppingMallOperationTag = new TagOption();
        shoppingMallOperationTag.setTagName("ShoppingMall Operation");
        shoppingMallOperationTag.setCategory(marketing);
        tagOptionRepository.save(shoppingMallOperationTag);

        TagOption SNSManagementTag = new TagOption();
        SNSManagementTag.setTagName("SNS Management");
        SNSManagementTag.setCategory(marketing);
        tagOptionRepository.save(SNSManagementTag);

        TagOption CPAMarketingTag = new TagOption();
        CPAMarketingTag.setTagName("CPA Marketing");
        CPAMarketingTag.setCategory(marketing);
        tagOptionRepository.save(CPAMarketingTag);

        // 3. 테스트 학생에 태그 연결
        Student student1 = studentRepository.findById(1L).orElse(null);
        if (student1 != null) {
            student1.getTags().addAll(List.of(javaTag, springTag, sqlTag));
            studentRepository.save(student1);
        }
        Student student2 = studentRepository.findById(2L).orElse(null);
        if (student2 != null) {
            student2.getTags().addAll(List.of(adobePhotoshopTag, adobeIllustratorTag));
            studentRepository.save(student2);
        }
        Student student3 = studentRepository.findById(3L).orElse(null);
        if (student3 != null) {
            student3.getTags().addAll(List.of(SNSManagementTag, CPAMarketingTag));
            studentRepository.save(student3);
        }

        System.out.println("✅ TagInitializer: 더미 데이터 초기화 완료!");
    }
}
