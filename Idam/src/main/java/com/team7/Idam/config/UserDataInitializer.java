package com.team7.Idam.config;

import com.team7.Idam.domain.user.entity.*;
import com.team7.Idam.domain.user.entity.enums.*;
import com.team7.Idam.domain.user.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PortfolioRepository portfolioRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // ─── 1️⃣ 학생 더미 10명 생성 ───────────────────────────────
        for (int i = 1; i <= 10; i++) {
            final int idx = i;
            String email = String.format("student%02d@example.com", idx);

            userRepository.findByEmail(email).ifPresentOrElse(
                    existing -> { /* 이미 있으면 건너뜀 */ },
                    () -> {
                        // 1-1. User 저장
                        User studentUser = userRepository.save(
                                User.builder()
                                        .email(email)
                                        .userType(UserType.STUDENT)
                                        .userStatus(UserStatus.ACTIVE)
                                        .phone(String.format("010-1234-%04d",
                                                ThreadLocalRandom.current().nextInt(1000, 10000)))
                                        .build()
                        );
                        // IDENTITY 전략이므로 save() 시 즉시 ID 할당됨

                        // 1-2. Student 저장 (@MapsId 자동 처리)
                        Student student = studentRepository.save(
                                Student.builder()
                                        .user(studentUser)
                                        .name("학생" + idx)
                                        .nickname("stud" + idx)
                                        .schoolName("인천대학교")
                                        .major("정보통신공학과")
                                        .schoolId("2023" + String.format("%05d", idx))
                                        .password("{noop}student1234")
                                        .profileImage("default-student.jpg")
                                        .gender((idx % 2 == 0) ? Gender.FEMALE : Gender.MALE)
                                        .build()
                        );

                        // 1-3. Portfolio 1~3개 랜덤 생성
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

        // ─── 2️⃣ 기업 더미 3개 생성 ───────────────────────────────
        for (int i = 1; i <= 3; i++) {
            final int idx = i;
            String email = String.format("company%02d@example.com", idx);

            userRepository.findByEmail(email).ifPresentOrElse(
                    existing -> { /* 이미 있으면 건너뜀 */ },
                    () -> {
                        // 2-1. User 저장
                        User companyUser = userRepository.save(
                                User.builder()
                                        .email(email)
                                        .userType(UserType.COMPANY)
                                        .userStatus(UserStatus.ACTIVE)
                                        .phone(String.format("02-9876-%04d",
                                                ThreadLocalRandom.current().nextInt(1000, 10000)))
                                        .build()
                        );

                        // 2-2. Company 저장
                        companyRepository.save(
                                Company.builder()
                                        .user(companyUser)
                                        .password("{noop}company1234")  // 실제론 BCrypt 적용 권장
                                        .businessRegistrationNumber(
                                                String.format("%03d-%02d-%05d",
                                                        ThreadLocalRandom.current().nextInt(100, 1000),
                                                        ThreadLocalRandom.current().nextInt(10, 100),
                                                        ThreadLocalRandom.current().nextInt(10000, 100000)
                                                )
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
    }
}
