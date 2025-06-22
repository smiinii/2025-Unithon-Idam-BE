package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.profile.company.CompanyPreviewResponseDto;
import com.team7.Idam.domain.user.dto.profile.company.CompanyProfileResponseDto;
import com.team7.Idam.domain.user.dto.profile.company.CompanyProfileUpdateRequestDto;
import com.team7.Idam.domain.user.service.CompanyProfileService;
import com.team7.Idam.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyProfileController {

    private final CompanyProfileService companyService;

    // 프로필 전체 조회
    @GetMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<CompanyProfileResponseDto>> getCompanyProfile(@PathVariable Long userId) {
        CompanyProfileResponseDto profile = companyService.getCompanyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", profile));
    }

    // 프로필 이미지 추가/수정
    @PutMapping("/{userId}/profile/image")
    public ResponseEntity<ApiResponse<Void>> updateProfileImage(@PathVariable Long userId, @RequestPart("profileImage") MultipartFile file) {
        companyService.updateProfileImage(userId, file);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 수정되었습니다."));
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/{userId}/profile/image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(@PathVariable Long userId) {
        companyService.deleteProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필 이미지가 삭제되었습니다."));
    }

    // 프로필 정보 수정
    @PatchMapping("/{userId}/profile")
    public ResponseEntity<ApiResponse<Void>> updateCompanyProfile(@PathVariable Long userId, @RequestBody CompanyProfileUpdateRequestDto request) {
        companyService.updateCompanyProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("프로필이 수정되었습니다."));
    }

    // 메인 화면 Preview용 모든 기업 조회
    @GetMapping("/preview")
    public ResponseEntity<ApiResponse<List<CompanyPreviewResponseDto>>> getAllStudents() {
        return ResponseEntity.ok(
                ApiResponse.success("모든 기업 조회 성공", companyService.getAllStudents())
        );
    }

}
