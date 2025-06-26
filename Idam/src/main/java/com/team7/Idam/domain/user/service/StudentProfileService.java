package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.profile.student.*;
import com.team7.Idam.domain.user.entity.Portfolio;
import com.team7.Idam.domain.user.entity.Student;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.repository.PortfolioRepository;
import com.team7.Idam.domain.user.repository.StudentRepository;
import com.team7.Idam.domain.user.repository.TagOptionRepository;
import com.team7.Idam.global.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final PortfolioRepository portfolioRepository;
    private final TagOptionRepository tagOptionRepository;
    private final FileUploadService fileUploadService;

    // AccessToken 사용자 == 요청된 UserId 일치할 때만 로직 수행.
    public void validateStudentAccess(Long requestedUserId) {
        Long loginUserId = SecurityUtil.getCurrentUserId(); // JWT 기반 사용자 ID
        if (!loginUserId.equals(requestedUserId)) {
            throw new AccessDeniedException("자신의 정보에만 접근할 수 있습니다.");
        }
    }

    /*
        학생 프로필 전체 조회
     */
    public StudentProfileResponseDto getStudentProfile(Long userId) {

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        // 태그 리스트
        List<String> tags = student.getTags().stream()
                .map(TagOption::getTagName)
                .collect(Collectors.toList());

        // 포트폴리오 리스트
        List<PortfolioResponseDto> portfolios = student.getPortfolios().stream()
                .map(PortfolioResponseDto::from)
                .toList();

        return StudentProfileResponseDto.builder()
                .name(student.getName())
                .schoolName(student.getSchoolName())
                .major(student.getMajor())
                .schoolId(student.getSchoolId())
                .nickname(student.getNickname())
                .gender(student.getGender())
                .profileImage(student.getProfileImage())
                .email(student.getUser().getEmail())
                .phone(student.getUser().getPhone())
                .categoryId(student.getCategory().getId())
                .tags(tags)
                .portfolios(portfolios)
                .build();
    }

    /*
        학생 프로필 정보 수정
     */
    @Transactional
    public void updateStudentProfile(Long userId, StudentProfileUpdateRequestDto request) {
        validateStudentAccess(userId);

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        if (request.getNickname() != null) {
            student.setNickname(request.getNickname());
        }
        if (request.getGender() != null) {
            student.setGender(request.getGender());
        }
        if (request.getEmail() != null) {
            student.getUser().setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            student.getUser().setPhone(request.getPhone());
        }

        studentRepository.save(student);
    }

    /*
        프로필 이미지 업로드
     */
    public void updateProfileImage(Long userId, MultipartFile file) {
        validateStudentAccess(userId);

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        if (student.getProfileImage() != null) { // 프로필 이미지 이미 존재 시
            fileUploadService.delete(student.getProfileImage()); // S3에서 삭제
        }

        String imageUrl = fileUploadService.upload(file); // S3에 저장

        student.setProfileImage(imageUrl);
        studentRepository.save(student);
    }

    /*
        프로필 이미지 삭제
     */
    public void deleteProfileImage(Long userId) {
        validateStudentAccess(userId);

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        String imageUrl = student.getProfileImage();
        if (imageUrl == null) {
            throw new SecurityException("삭제할 프로필 이미지가 없습니다.");
        }

        fileUploadService.delete(imageUrl); // S3에서 삭제

        student.setProfileImage(null);
        studentRepository.save(student);
    }

    /*
        포트폴리오 File 추가
     */
    @Transactional
    public void addPortfolioFile(Long userId, MultipartFile file) {
        // 비어있을 때
        if (file == null) {
            throw new IllegalArgumentException("파일이 업로드되지 않았습니다.");
        }

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        String portfolioFile = fileUploadService.upload(file);

        boolean exists = portfolioRepository.existsByStudentIdAndPortfolio(userId, portfolioFile);
        if (exists) {
            throw new IllegalArgumentException("이미 동일한 포트폴리오 파일이 등록되어 있습니다.");
        }

        Portfolio portfolio = Portfolio.builder()
                .student(student)
                .portfolio(portfolioFile)
                .build();

        portfolioRepository.save(portfolio);
    }

    /*
        포트폴리오 URL 추가
     */
    @Transactional
    public void addPortfolioUrl(Long userId, String url) {
        // 비어있을 때
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL이 입력되지 않았습니다.");
        }

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        boolean exists = portfolioRepository.existsByStudentIdAndPortfolio(userId, url);
        if (exists) {
            throw new IllegalArgumentException("이미 동일한 포트폴리오 주소가 등록되어 있습니다.");
        }

        Portfolio portfolio = Portfolio.builder()
                .student(student)
                .portfolio(url)
                .build();

        portfolioRepository.save(portfolio);
    }

    /*
        포트폴리오 삭제
     */
    @Transactional
    public void deletePortfolio(Long userId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getStudent().getId().equals(userId)) {
            throw new SecurityException("본인만 삭제할 수 있습니다.");
        }

        String portfolioValue = portfolio.getPortfolio();

        // 파일 형식이면 S3에서 삭제
        if (isS3FileUrl(portfolioValue)) {
            fileUploadService.delete(portfolioValue);
        }

        portfolioRepository.delete(portfolio);
    }

    // 간단한 S3 URL 판별 메서드
    private boolean isS3FileUrl(String url) {
        return url != null && url.contains(".s3.") && url.contains("amazonaws.com");
    }

    /*
        태그 추가
     */
    @Transactional
    public void updateStudentTagsByName(Long userId, Long categoryId, UpdateTagsRequestDto request) {
        validateStudentAccess(userId);

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        List<String> tagNames = request.getTags();

        // 카테고리 내에서만 태그 찾기
        List<TagOption> tags = tagOptionRepository.findAllByTagNameInAndCategoryId(tagNames, categoryId);

        if (tags.size() != tagNames.size()) {
            throw new IllegalArgumentException("해당 카테고리에서 일치하지 않는 태그명이 존재합니다.");
        }

        // 기존 태그 이름들을 소문자로 변환하여 Set 구성
        Set<String> existingTagNamesLower = student.getTags().stream()
                .map(tag -> tag.getTagName().toLowerCase())
                .collect(Collectors.toSet());

        // 요청된 태그 중 대소문자 무시하고 중복된 것 찾기
        List<String> duplicateTagNames = tagNames.stream()
                .filter(tag -> existingTagNamesLower.contains(tag.toLowerCase()))
                .toList();

        if (!duplicateTagNames.isEmpty()) {
            throw new IllegalArgumentException("이미 등록된 태그입니다: " + String.join(", ", duplicateTagNames));
        }

        // 중복 없으니 추가
        Set<TagOption> updatedTags = new HashSet<>(student.getTags());
        updatedTags.addAll(tags);
        student.setTags(updatedTags);

        student.setTags(updatedTags);
        studentRepository.save(student);
    }

    /*
        태그 삭제
     */
    @Transactional
    public void deleteStudentTagsByName(Long userId, Long categoryId, UpdateTagsRequestDto request) {
        validateStudentAccess(userId);

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        List<String> tagNames = request.getTags();

        // 카테고리 내에서만 태그 찾기
        List<TagOption> tagsToRemove = tagOptionRepository.findAllByTagNameInAndCategoryId(tagNames, categoryId);

        if (tagsToRemove.size() != tagNames.size()) {
            throw new IllegalArgumentException("해당 카테고리에서 일치하지 않는 태그명이 존재합니다.");
        }

        student.getTags().removeAll(tagsToRemove);
        studentRepository.save(student);
    }

    /*
        메인 화면 Preview용 모든 학생 조회 (카테고리 별 랜덤 4명)
     */
    public List<StudentPreviewResponseDto> getAllStudents() {
        List<Student> allStudents = studentRepository.findAll();
        Collections.shuffle(allStudents); // 전체를 섞은 뒤 카테고리별로 추출

        List<StudentPreviewResponseDto> result = new ArrayList<>();

        // 각 카테고리에서 4명씩 추출
        for (long categoryId = 1; categoryId <= 3; categoryId++) {
            final long cid = categoryId;  // 람다용 임시 final 변수

            List<StudentPreviewResponseDto> selected = allStudents.stream()
                    .filter(student -> student.getCategory().getId().equals(cid))
                    .limit(4)
                    .map(StudentPreviewResponseDto::from)
                    .collect(Collectors.toList());

            result.addAll(selected);
        }

        return result;
    }
}
