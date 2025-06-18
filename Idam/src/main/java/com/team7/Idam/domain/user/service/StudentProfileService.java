package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.profile.PortfolioResponseDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileResponseDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileUpdateRequestDto;
import com.team7.Idam.domain.user.dto.profile.UpdateTagsRequestDto;
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

import java.util.HashSet;
import java.util.List;
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
                .map(p -> new PortfolioResponseDto(p.getPortfolioId(), p.getPortfolio()))
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
        포트폴리오 추가
     */
    @Transactional
    public void addPortfolio(Long userId, MultipartFile file, String url) {
        // 둘 다 비어있을 때
        if (file == null && (url == null || url.isBlank())) {
            throw new IllegalArgumentException("파일 또는 URL 중 하나는 반드시 입력되어야 합니다.");
        }

        // 둘 다 있을 때 → 허용하지 않음
        if (file != null && url != null && !url.isBlank()) {
            throw new IllegalArgumentException("불가능한 접근입니다.");
        }

        Student student = studentRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        String portfolioValue = (file != null)
                ? fileUploadService.upload(file)
                : url;

        boolean exists = portfolioRepository.existsByStudentIdAndPortfolio(userId, portfolioValue);
        if (exists) {
            throw new IllegalArgumentException("이미 동일한 포트폴리오가 등록되어 있습니다.");
        }

        Portfolio portfolio = Portfolio.builder()
                .student(student)
                .portfolio(portfolioValue)
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
        태그 추가/수정
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

        student.setTags(new HashSet<>(tags));
        studentRepository.save(student);
    }
}
