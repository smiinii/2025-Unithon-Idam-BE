package com.team7.Idam.config;

import com.team7.Idam.domain.user.entity.*;
import com.team7.Idam.domain.user.entity.enums.*;
import com.team7.Idam.domain.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PortfolioRepository portfolioRepository;
    private final TagCategoryRepository tagCategoryRepository;
    private final TagOptionRepository tagOptionRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 카테고리 목록
        List<String> categoryNames = List.of("IT·프로그래밍", "디자인", "마케팅");

        int studentId = 1;

        // 1. 학생 더미 생성
        for (String categoryName : categoryNames) {
            TagCategory category = tagCategoryRepository.findByCategoryName(categoryName)
                    .orElseThrow(() -> new IllegalStateException("카테고리 '" + categoryName + "'이 존재하지 않습니다."));
            List<TagOption> tags = tagOptionRepository.findAllByCategory(category);

            for (int i = 1; i <= 10; i++, studentId++) {
                String email = String.format("student%02d@example.com", studentId);
                if (userRepository.findByEmail(email).isPresent()) continue;

                User studentUser = userRepository.save(
                        User.builder()
                                .email(email)
                                .userType(UserType.STUDENT)
                                .userStatus(UserStatus.ACTIVE)
                                .phone(String.format("010-1234-%04d", ThreadLocalRandom.current().nextInt(1000, 10000)))
                                .build()
                );

                Student student = studentRepository.save(
                        Student.builder()
                                .user(studentUser)
                                .name("학생" + studentId)
                                .nickname("stud" + studentId)
                                .schoolName("인천대학교")
                                .major("정보통신공학과")
                                .schoolId("2023" + String.format("%05d", studentId))
                                .password("student1234")
                                .gender((studentId % 2 == 0) ? Gender.FEMALE : Gender.MALE)
                                .category(category)
                                .build()
                );

                // 태그 5~15개 랜덤 연결
                Collections.shuffle(tags);
                student.setTags(new HashSet<>(tags.subList(0, ThreadLocalRandom.current().nextInt(5, Math.min(tags.size(), 15) + 1))));
                studentRepository.save(student);

                // 포트폴리오 1~3개 생성
                int count = ThreadLocalRandom.current().nextInt(1, 4);
                for (int j = 1; j <= count; j++) {
                    portfolioRepository.save(
                            Portfolio.builder()
                                    .student(student)
                                    .portfolio(String.format("student%02d_portfolio%d.pdf", studentId, j))
                                    .build()
                    );
                }
            }
        }

        // 2. 기업 더미 생성
        for (int i = 1; i <= 3; i++) {
            String email = String.format("company%02d@example.com", i);
            if (userRepository.findByEmail(email).isPresent()) continue;

            User companyUser = userRepository.save(
                    User.builder()
                            .email(email)
                            .userType(UserType.COMPANY)
                            .userStatus(UserStatus.ACTIVE)
                            .phone(String.format("02-9876-%04d", ThreadLocalRandom.current().nextInt(1000, 10000)))
                            .build()
            );

            companyRepository.save(
                    Company.builder()
                            .user(companyUser)
                            .password("{noop}company1234")
                            .businessRegistrationNumber(
                                    String.format("%03d-%02d-%05d",
                                            ThreadLocalRandom.current().nextInt(100, 1000),
                                            ThreadLocalRandom.current().nextInt(10, 100),
                                            ThreadLocalRandom.current().nextInt(10000, 100000))
                            )
                            .companyName("기업사" + i)
                            .address("서울시 강남구 테헤란로 " + (10 + i))
                            .website("https://company" + i + ".example.com")
                            .companyDescription("기업 소개가 없습니다.")
                            .build()
            );
        }

        System.out.println("유저 및 포트폴리오 더미 데이터 생성 완료.");
    }
}
