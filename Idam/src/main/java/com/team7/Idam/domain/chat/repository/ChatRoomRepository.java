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

    Optional<ChatRoom> findByCompanyAndStudentAndIsDeletedByCompanyFalseAndIsDeletedByStudentFalse(User company, User student);

    @Query("SELECT r FROM ChatRoom r LEFT JOIN FETCH r.messages WHERE r.id = :id")
    Optional<ChatRoom> findWithMessagesById(@Param("id") Long id);

}