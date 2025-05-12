package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.profile.PortfolioRequestDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileResponseDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileUpdateRequestDto;
import com.team7.Idam.domain.user.entity.Portfolio;
import com.team7.Idam.domain.user.entity.Student;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.repository.PortfolioRepository;
import com.team7.Idam.domain.user.repository.StudentRepository;
import com.team7.Idam.domain.user.repository.TagOptionRepository;
import com.team7.Idam.jwt.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentRepository studentRepository;
    private final PortfolioRepository portfolioRepository;
    private final TagOptionRepository tagOptionRepository;
    private final FileUploadService fileUploadService;

    /*
        학생 프로필 전체 조회
     */
    public StudentProfileResponseDto getStudentProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

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
                .build();
    }

    /*
        학생 프로필 정보 수정
     */
    @Transactional
    public void updateStudentProfile(Long studentId, StudentProfileUpdateRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        Long currentUserId = getCurrentUserId();  // JWT에서 추출
        if (!student.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("본인만 수정할 수 있습니다.");
        }

        if (request.getNickname() != null) {
            student.setNickname(request.getNickname());
        }
        if (request.getGender() != null) {
            student.setGender(request.getGender());
        }

        studentRepository.save(student);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    /*
        프로필 이미지 업로드
     */
    public void updateProfileImage(Long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId)
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
    public void deleteProfileImage(Long studentId) {
        Student student = studentRepository.findById(studentId)
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
    public void addPortfolio(Long studentId, PortfolioRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        boolean exists = portfolioRepository.existsByStudentIdAndPortfolio(studentId, request.getPortfolio());
        if (exists) {
            throw new IllegalArgumentException("이미 동일한 포트폴리오가 등록되어 있습니다.");
        }

        Portfolio portfolio = Portfolio.builder()
                .student(student)
                .portfolio(request.getPortfolio())
                .build();

        portfolioRepository.save(portfolio);
    }

    /*
        포트폴리오 삭제
     */
    @Transactional
    public void deletePortfolio(Long studentId, Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new IllegalArgumentException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getStudent().getId().equals(studentId)) {
            throw new SecurityException("본인만 삭제할 수 있습니다.");
        }

        portfolioRepository.delete(portfolio);
    }

    /*
        태그 추가/수정
     */
    @Transactional
    public void updateStudentTagsByName(Long studentId, Long categoryId, List<String> tagNames) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("학생을 찾을 수 없습니다."));

        // 카테고리 내에서만 태그 찾기
        List<TagOption> tags = tagOptionRepository.findAllByTagNameInAndCategoryId(tagNames, categoryId);

        if (tags.size() != tagNames.size()) {
            throw new IllegalArgumentException("해당 카테고리에서 일치하지 않는 태그명이 존재합니다.");
        }

        student.setTags(new HashSet<>(tags));
        studentRepository.save(student);
    }
}
