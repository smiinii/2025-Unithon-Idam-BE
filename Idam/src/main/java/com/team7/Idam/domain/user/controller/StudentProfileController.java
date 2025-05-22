package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.profile.StudentProfileResponseDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileUpdateRequestDto;
import com.team7.Idam.domain.user.service.StudentProfileService;
import com.team7.Idam.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentService;

    // 프로필 전체 조회
    @GetMapping("/{studentId}/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> getStudentProfile(@PathVariable Long studentId) {
        StudentProfileResponseDto profile = studentService.getStudentProfile(studentId);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", profile));
    }

    // 프로필 정보 수정
    @PatchMapping("/{studentId}/profile")
    public ResponseEntity<ApiResponse<Void>> updateStudentProfile(@PathVariable Long studentId, @RequestBody StudentProfileUpdateRequestDto request) {
        studentService.updateStudentProfile(studentId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다."));
    }

    // 프로필 이미지 추가/수정
    @PutMapping("/{studentId}/profile/image")
    public ResponseEntity<ApiResponse<Void>> updateProfileImage(@PathVariable Long studentId, @RequestPart("profileImage") MultipartFile file) {
        studentService.updateProfileImage(studentId, file);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 수정되었습니다."));
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/{studentId}/profile/image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@PathVariable Long studentId) {
        studentService.deleteProfileImage(studentId);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 삭제되었습니다."));
    }

    // 포트폴리오 추가
    @PostMapping("/{studentId}/portfolios")
    public ResponseEntity<ApiResponse<Void>> addPortfolio(
            @PathVariable Long studentId,
            @RequestPart(required = false) MultipartFile portfolioFile,
            @RequestPart(required = false) String portfolioUrl) {

        studentService.addPortfolio(studentId, portfolioFile, portfolioUrl);
        return ResponseEntity.ok(ApiResponse.success("포트폴리오가 등록되었습니다."));
    }

    // 포트폴리오 삭제
    @DeleteMapping("/{studentId}/portfolios/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(@PathVariable Long studentId, @PathVariable Long portfolioId) {
        studentService.deletePortfolio(studentId, portfolioId);
        return ResponseEntity.ok(ApiResponse.success("포트폴리오가 삭제되었습니다."));
    }

    // 태그 추가/수정
    @PutMapping("/{studentId}/categories/{categoryId}/tags")
    public ResponseEntity<ApiResponse<Void>> updateStudentTags(@PathVariable Long studentId, @PathVariable Long categoryId, @RequestBody List<String> tagNames) {
        studentService.updateStudentTagsByName(studentId, categoryId, tagNames);
        return ResponseEntity.ok(ApiResponse.success("태그가 수정되었습니다."));
    }

}