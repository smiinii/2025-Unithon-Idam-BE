package com.team7.Idam.domain.user.dto.profile.student;

import com.team7.Idam.domain.user.entity.Student;
import com.team7.Idam.domain.user.entity.TagOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentPreviewResponseDto {
    private Long userId;
    private String name;
    private String profileImage;
    private List<String> tags;
    private Long categoryId;

    /*
        Student 도메인 객체를 StudentPreviewResponseDto로 변환하는 정적 팩토리 메서드.
     */
    public static StudentPreviewResponseDto from(Student student) {
        return new StudentPreviewResponseDto(
                student.getId(),
                student.getName(),
                student.getProfileImage(),
                student.getTags().stream()
                        .map(TagOption::getTagName)
                        .collect(Collectors.toList()),
                student.getCategory().getId()
        );
    }
}
