package com.team7.Idam.domain.user.controller;

import com.team7.Idam.domain.user.dto.profile.PortfolioRequestDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileResponseDto;
import com.team7.Idam.domain.user.dto.profile.StudentProfileUpdateRequestDto;
import com.team7.Idam.domain.user.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentProfileService studentService;

    // 프로필 전체 조회
    @GetMapping("/{studentId}/profile")
    public StudentProfileResponseDto getStudentProfile(@PathVariable Long studentId) {
        return studentService.getStudentProfile(studentId);
    }

    // 프로필 정보 수정
    @PatchMapping("/{studentId}/profile")
    public ResponseEntity<String> updateStudentProfile(@PathVariable Long studentId, @RequestBody StudentProfileUpdateRequestDto request) {
        studentService.updateStudentProfile(studentId, request);
        return ResponseEntity.ok().body("프로필이 수정되었습니다.");
    }

    // 프로필 이미지 추가/수정
    @PutMapping("/{studentId}/profile/image")
    public ResponseEntity<String> updateProfileImage(@PathVariable Long studentId, @RequestPart("profileImage") MultipartFile file) {
        studentService.updateProfileImage(studentId, file);
        return ResponseEntity.ok().body("프로필 이미지가 수정되었습니다.");
    }

    // 프로필 이미지 삭제
    @DeleteMapping("/{studentId}/profile/image")
    public ResponseEntity<String> deleteProfileImage(@PathVariable Long studentId) {
        studentService.deleteProfileImage(studentId);
        return ResponseEntity.ok().body("프로필 이미지가 삭제되었습니다.");
    }

    // 포트폴리오 추가
    @PostMapping("/{studentId}/portfolios")
    public ResponseEntity<String> addPortfolio(@PathVariable Long studentId, @RequestBody PortfolioRequestDto request) {
        studentService.addPortfolio(studentId, request);
        return ResponseEntity.ok().body("포트폴리오가 추가되었습니다.");
    }

    // 포트폴리오 삭제
    @DeleteMapping("/{studentId}/portfolios/{portfolioId}")
    public ResponseEntity<String> deletePortfolio(@PathVariable Long studentId, @PathVariable Long portfolioId) {
        studentService.deletePortfolio(studentId, portfolioId);
        return ResponseEntity.ok().body("포트폴리오가 삭제되었습니다.");
    }

    // 태그 추가
    @PostMapping("/{studentId}/tags/{tagId}")
    public ResponseEntity<String> addStudentTag(@PathVariable Long studentId, @PathVariable Long tagId) {
        studentService.addStudentTag(studentId, tagId);
        return ResponseEntity.ok().body("태그가 추가되었습니다.");
    }

    // 태그 수정
    @PutMapping("/{studentId}/tags")
    public ResponseEntity<String> updateStudentTags(@PathVariable Long studentId, @RequestBody List<Long> tagIds) {
        studentService.updateStudentTags(studentId, tagIds);
        return ResponseEntity.ok().body("태그가 수정되었습니다.");
    }

    // 태그 삭제
    @DeleteMapping("/{studentId}/tags/{tagId}")
    public ResponseEntity<String> removeStudentTag(@PathVariable Long studentId, @PathVariable Long tagId) {
        studentService.removeStudentTag(studentId, tagId);
        return ResponseEntity.ok().body("태그가 삭제되었습니다.");
    }
}