package com.team7.Idam.config;

import com.team7.Idam.domain.user.entity.*;
import com.team7.Idam.domain.user.entity.enums.*;
import com.team7.Idam.domain.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(2) // TagDataInitializer가 1번에 실행되도록 설정
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
        // 1. IT 카테고리 조회 (TagDataInitializer에서 이미 생성됨을 가정)
        TagCategory itCategory = tagCategoryRepository.findByCategoryName("IT·프로그래밍")
                .orElseThrow(() -> new IllegalStateException("카테고리 'IT·프로그래밍'이 존재하지 않습니다."));
        List<TagOption> itTags = tagOptionRepository.findAllByCategory(itCategory);

        // 2. 학생 더미 생성
        for (int i = 1; i <= 10; i++) {
            final int idx = i;
            String email = String.format("student%02d@example.com", idx);

            userRepository.findByEmail(email).ifPresentOrElse(
                    existing -> {}, // 이미 있으면 무시
                    () -> {
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
                                        .name("학생" + idx)
                                        .nickname("stud" + idx)
                                        .schoolName("인천대학교")
                                        .major("정보통신공학과")
                                        .schoolId("2023" + String.format("%05d", idx))
                                        .password("student1234")
                                        .profileImage("default-student.jpg")
                                        .gender((idx % 2 == 0) ? Gender.FEMALE : Gender.MALE)
                                        .category(itCategory)
                                        .build()
                        );

                        // 태그 랜덤 연결 (2~4개)
                        int tagCount = ThreadLocalRandom.current().nextInt(2, 5);
                        List<TagOption> shuffled = new ArrayList<>(itTags);
                        Collections.shuffle(shuffled);
                        student.setTags(new HashSet<>(shuffled.subList(0, tagCount)));
                        studentRepository.save(student);

                        // 포트폴리오 1~3개 생성
                        int count = ThreadLocalRandom.current().nextInt(1, 4);
                        for (int j = 1; j <= count; j++) {
                            portfolioRepository.save(
                                    Portfolio.builder()
                                            .student(student)
                                            .portfolio(String.format("student%02d_portfolio%d.pdf", idx, j))
                                            .build()
                            );
                        }
                    }
            );
        }

        // 3. 기업 더미 생성
        for (int i = 1; i <= 3; i++) {
            final int idx = i;
            String email = String.format("company%02d@example.com", idx);

            userRepository.findByEmail(email).ifPresentOrElse(
                    existing -> {},
                    () -> {
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
                                        .companyName("기업사" + idx)
                                        .address("서울시 강남구 테헤란로 " + (10 + idx))
                                        .website("https://company" + idx + ".example.com")
                                        .profileImage("default-company.jpg")
                                        .build()
                        );
                    }
            );
        }

        System.out.println("유저 및 포트폴리오 더미 데이터 생성 완료.");
    }
}
