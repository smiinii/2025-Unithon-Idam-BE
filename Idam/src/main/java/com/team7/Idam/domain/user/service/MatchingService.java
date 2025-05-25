package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.matching.RecommendRequestDto;
import com.team7.Idam.domain.user.dto.matching.ScoredStudentResponseDto;
import com.team7.Idam.domain.user.entity.Student;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.entity.enums.UserType;
import com.team7.Idam.domain.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.team7.Idam.global.util.SecurityUtil;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final StudentRepository studentRepository;

    public void validateCompanyAccess() {
        if (SecurityUtil.getCurrentUserType() != UserType.COMPANY) {
            throw new AccessDeniedException("해당 기능은 기업 회원만 사용할 수 있습니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<ScoredStudentResponseDto> recommendStudentsByCategory(RecommendRequestDto request) {
        validateCompanyAccess(); // 기업 타입만 실행 가능

        Long categoryId = request.getCategoryId();
        List<String> requestedTags = request.getTags();

        // 해당 분야(categoryId)의 학생들 조회
        List<Student> students = studentRepository.findAllByCategoryId(categoryId);

        if (students.isEmpty()) {
            throw new IllegalArgumentException("해당 분야에 등록된 학생이 존재하지 않습니다.");
        }

        // 태그 이름 Set으로 변환 (contains 성능 ↑)
        Set<String> tagNameSet = new HashSet<>(requestedTags);

        // 점수 계산 및 DTO 리스트 생성
        List<ScoredStudentResponseDto> result = new ArrayList<>();

        for (Student student : students) {
            int score = (int) student.getTags().stream()
                    .map(TagOption::getTagName)
                    .filter(tagNameSet::contains)
                    .count();

            result.add(new ScoredStudentResponseDto(student.getId(), student.getName(), score));
        }

        // 점수 기준 내림차순 정렬 후 상위 5명 반환
        return result.stream()
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .limit(5)
                .toList();
    }
}
