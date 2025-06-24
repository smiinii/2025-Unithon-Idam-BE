package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.profile.student.StudentPreviewResponseDto;
import com.team7.Idam.domain.user.dto.profile.student.StudentProfileResponseDto;
import com.team7.Idam.domain.user.dto.profile.student.StudentProfileUpdateRequestDto;
import com.team7.Idam.domain.user.dto.profile.student.UpdateTagsRequestDto;
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
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponseDto>> getStudentProfile(@PathVariable Long userId) {
        StudentProfileResponseDto profile = studentService.getStudentProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", profile));
    }

    // 프로필 정보 수정
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<Void>> updateStudentProfile(@PathVariable Long userId, @RequestBody StudentProfileUpdateRequestDto request) {
        studentService.updateStudentProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다."));
    }

    // 프로필 이미지 추가/수정
    @PutMapping("/{userId}/profile/image")
    public ResponseEntity<ApiResponse<Void>> updateProfileImage(@PathVariable Long userId, @RequestPart("profileImage") MultipartFile file) {
        studentService.updateProfileImage(userId, file);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 수정되었습니다."));
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/{userId}/profile/image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@PathVariable Long userId) {
        studentService.deleteProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 삭제되었습니다."));
    }

    // 포트폴리오 추가
    @PostMapping("/{userId}/portfolios")
    public ResponseEntity<ApiResponse<Void>> addPortfolio(
            @PathVariable Long userId,
            @RequestPart(required = false) MultipartFile portfolioFile,
            @RequestPart(required = false) String portfolioUrl) {

        studentService.addPortfolio(userId, portfolioFile, portfolioUrl);
        return ResponseEntity.ok(ApiResponse.success("포트폴리오가 등록되었습니다."));
    }

    // 포트폴리오 삭제
    @DeleteMapping("/{userId}/portfolios/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(@PathVariable Long userId, @PathVariable Long portfolioId) {
        studentService.deletePortfolio(userId, portfolioId);
        return ResponseEntity.ok(ApiResponse.success("포트폴리오가 삭제되었습니다."));
    }

    // 태그 추가
    @PatchMapping("/{userId}/categories/{categoryId}/tags")
    public ResponseEntity<ApiResponse<Void>> updateStudentTags(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestBody UpdateTagsRequestDto request
    ) {
        studentService.updateStudentTagsByName(userId, categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("태그가 추가되었습니다."));
    }

    // 태그 삭제
    @DeleteMapping("/{userId}/categories/{categoryId}/tags")
    public ResponseEntity<ApiResponse<Void>> deleteStudentTags(
            @PathVariable Long userId,
            @PathVariable Long categoryId,
            @RequestBody UpdateTagsRequestDto request
    ) {
        studentService.deleteStudentTagsByName(userId, categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("태그가 삭제되었습니다."));
    }

    // 메인 화면 Preview용 모든 학생 조회
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<List<StudentPreviewResponseDto>>> getAllStudents() {
        return ResponseEntity.ok(
                ApiResponse.success("모든 학생 조회 성공", studentService.getAllStudents())
        );
    }
}