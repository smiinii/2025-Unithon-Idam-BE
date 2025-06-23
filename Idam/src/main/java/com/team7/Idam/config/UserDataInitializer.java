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

        //  가상 학생 이름 리스트
        List<String> studentNames = List.of(
                "김지훈", "이서연", "박준영", "최지우", "정민서", "한예린", "장하준", "윤서아", "백현우", "조하늘",
                "오유진", "남도현", "서지민", "이하은", "문지후", "송다인", "임하랑", "강시아", "배시우", "허나윤",
                "노지아", "하채민", "전지후", "구세린", "류현우", "신다원", "고예성", "권하람", "민서준", "최가을"
        );
        // 30개 실제 이미지 URL (AWS S3 경로)
        List<String> studentImages = List.of(
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/1.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/2.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/3.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/4.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/5.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/6.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/7.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/8.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/9.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/10.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/11.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/12.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/13.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/14.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/15.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/16.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/17.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/18.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/19.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/20.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/21.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/22.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/23.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/24.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/25.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/26.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/27.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/28.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/29.jpeg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/30.jpeg"
        );
        // 가상 학생 닉네임 리스트
        List<String> studentNicknames = List.of(
                "codefox12", "skywalker07", "devminjae", "junoByte", "tech_jihun",
                "seoyeon_in", "junyoung99", "jiwoo_dev", "minseo.ai", "yerinXD",
                "hajun_studio", "seoah__design", "hyunwoo.coder", "blue_haneul", "yujin_star",
                "dohyun_devv", "zimin_says", "haeun.tech", "jihoolog", "dain_lab",
                "harang.codes", "sia_pixel", "siwoo_js", "nayoon_dev", "jia_flow",
                "chaeminUX", "jihuu_node", "serin_works", "hyunwoo23", "gaul_dev"
        );

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
                                .name(studentNames.get(studentId - 1))
                                .nickname(studentNicknames.get(studentId - 1))
                                .schoolName("인천대학교")
                                .major("정보통신공학과")
                                .schoolId("2023" + String.format("%05d", studentId))
                                .password("student1234")
                                .gender((studentId % 2 == 0) ? Gender.FEMALE : Gender.MALE)
                                .category(category)
                                .profileImage(studentImages.get(studentId - 1))
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

        // 실제 기업 정보 리스트 (10개)
        List<String> companyNames = List.of("(주)우아한형제들", "케이지이니시스", "젬텍", "한국멘토그래픽스", "가람디자인", "유윈디자인", "씨앤씨인터내셔널", "주식회사 진이어스", "TBWA코리아(주)", "엔비티");
        List<String> companyImages = List.of(
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/101.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/102.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/103.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/104.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/105.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/106.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/107.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/108.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/109.jpg",
                "https://unithon-idam.s3.ap-northeast-2.amazonaws.com/110.jpg"
        );
        List<String> companyWebsites = List.of(
                "http://www.woowahan.com/", "http://www.inicis.com/", "http://gemtek.co.kr/", "https://eda.sw.siemens.com/",
                "http://www.karamdesign.kr/", "https://www.youwindesign.com/", "http://www.cnccosmetic.com/", "http://www.geni-us.co.kr/",
                "http://www.tbwakorea.com/", "http://nbt.com/"
        );
        List<String> companyDescriptions = List.of(
                "배달의민족을 운영하는 국내 대표 배달 플랫폼 기업으로, IT를 활용한 음식 배달 서비스 혁신을 선도하고 있습니다.", // (주)우아한형제들
                "전자결제 서비스 및 핀테크 솔루션을 제공하는 KG그룹 계열사로, 다양한 온라인 결제 인프라를 보유하고 있습니다.", // 케이지이니시스
                "IoT와 네트워크 기술을 기반으로 무선 통신 기기 및 보안 시스템을 개발하는 전문 제조업체입니다.", // 젬텍
                "반도체 설계 자동화(EDA) 솔루션을 제공하는 글로벌 기업 Siemens EDA의 한국 지사입니다.", // 한국멘토그래픽스
                "공간 디자인과 전시, 브랜드 환경 구축 등을 수행하는 디자인 전문 기업으로, 창의적 컨셉 기획에 강점을 가집니다.", // 가람디자인
                "브랜딩, UI/UX, 마케팅 등 다양한 디자인 솔루션을 제공하는 통합 디자인 에이전시입니다.", // 유윈디자인
                "색조 화장품 ODM/OEM 전문 기업으로, 글로벌 브랜드와 협업하며 고기능성 제품을 연구 및 생산합니다.", // 씨앤씨인터내셔널
                "디지털 헬스케어와 라이프스타일 제품을 개발하는 스타트업으로, 기술과 디자인 융합에 주력합니다.", // 주식회사 진이어스
                "글로벌 광고대행사 TBWA의 한국 지사로, 크리에이티브 중심의 브랜드 캠페인과 마케팅 전략을 제공합니다.", // TBWA코리아(주)
                "모바일 리워드 플랫폼 '캐시슬라이드'를 운영하며, 광고주와 사용자를 연결하는 스마트 마케팅 서비스를 제공합니다." // 엔비티
        );

        // 2. 기업 더미 생성
        for (int i = 0; i < 10; i++) {
            String email = String.format("company%02d@example.com", i + 1);
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
                            .companyName(companyNames.get(i))
                            .address("서울시 강남구 테헤란로 " + (10 + i))
                            .website(companyWebsites.get(i))
                            .companyDescription(companyDescriptions.get(i))
                            .profileImage(companyImages.get(i))
                            .build()
            );
        }

        System.out.println("유저 및 포트폴리오 더미 데이터 생성 완료.");
    }
}
