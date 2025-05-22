package com.team7.Idam.domain.chat.repository;

import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 기업이 보는 채팅방 목록
    List<ChatRoom> findByCompanyAndIsDeletedByCompanyFalse(User company);

    // 학생이 보는 채팅방 목록
    List<ChatRoom> findByStudentAndIsDeletedByStudentFalse(User student);

    // 둘 다 삭제하지 않은 채팅방 조회
    @Query("SELECT c FROM ChatRoom c WHERE c.company = :company AND c.student = :student " +
            "AND c.isDeletedByCompany = false AND c.isDeletedByStudent = false")
    Optional<ChatRoom> findActiveRoomByUsers(@Param("company") User company, @Param("student") User student);

    // 기업이 학생 닉네임으로 검색
    @Query("SELECT r FROM ChatRoom r " +
            "WHERE r.company = :company AND r.isDeletedByCompany = false " +
            "AND r.student.id IN (" +
            "   SELECT s.id FROM Student s WHERE s.nickname LIKE %:keyword%" +
            ")")
    List<ChatRoom> searchRoomsByStudentNickname(
            @Param("company") User company,
            @Param("keyword") String keyword
    );

    // 학생이 기업 이름으로 검색
    @Query("SELECT r FROM ChatRoom r " +
            "WHERE r.student = :student AND r.isDeletedByStudent = false " +
            "AND r.company.id IN (" +
            "   SELECT c.id FROM Company c WHERE c.companyName LIKE %:keyword%" +
            ")")
    List<ChatRoom> searchRoomsByCompanyName(
            @Param("student") User student,
            @Param("keyword") String keyword
    );

}