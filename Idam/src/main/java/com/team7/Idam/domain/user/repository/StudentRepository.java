package com.team7.Idam.domain.user.repository;

import com.team7.Idam.domain.user.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByNickname(String nickname); // 별명 중복 방지
    boolean existsBySchoolId(String schoolId); // 학번 중복 방지

    List<Student> findAllByCategoryId(Long categoryId);
    List<Student> findDistinctByTags_CategoryId(Long categoryId);
}