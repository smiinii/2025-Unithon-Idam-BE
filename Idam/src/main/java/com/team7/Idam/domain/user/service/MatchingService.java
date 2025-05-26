package com.team7.Idam.domain.user.service;

import com.team7.Idam.domain.user.dto.matching.RecommendRequestDto;
import com.team7.Idam.domain.user.dto.matching.ScoredStudentResponseDto;
import com.team7.Idam.domain.user.entity.Student;
import com.team7.Idam.domain.user.entity.TagOption;
import com.team7.Idam.domain.user.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<ScoredStudentResponseDto> recommendStudentsByCategory(RecommendRequestDto request) {

        Long categoryId = request.getCategoryId();
        List<String> requestedTags = request.getTags();

        // 태그 정규화: 소문자 + 공백 제거
        Set<String> tagNameSet = requestedTags.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<Student> students = studentRepository.findAllByCategoryId(categoryId);

        if (students.isEmpty()) {
            throw new IllegalArgumentException("해당 분야에 등록된 학생이 존재하지 않습니다.");
        }

        List<ScoredStudentResponseDto> result = new ArrayList<>();

        for (Student student : students) {
            int score = (int) student.getTags().stream()
                    .map(TagOption::getTagName)
                    .filter(t -> t != null && !t.isBlank())
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(tagNameSet::contains)
                    .count();

            result.add(new ScoredStudentResponseDto(student.getId(), student.getName(), score));
        }

        return result.stream()
                .sorted((a, b) -> Integer.compare(b.getScore(), a.getScore()))
                .limit(5)
                .toList();
    }
}
