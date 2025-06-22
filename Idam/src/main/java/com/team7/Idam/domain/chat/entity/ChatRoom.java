package com.team7.Idam.domain.chat.entity;

import com.team7.Idam.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "chat_room")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 참여자들
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_user_id", nullable = false)
    private User company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_user_id", nullable = false)
    private User student;

    // 마지막 메시지 캐싱
    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 삭제 여부 (soft delete)
    @Column(name = "is_deleted_by_company", nullable = false)
    @Builder.Default
    private boolean isDeletedByCompany = false;

    // 삭제 여부 (soft delete)
    @Column(name = "is_deleted_by_student", nullable = false)
    @Builder.Default
    private boolean isDeletedByStudent = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    // 마지막 메시지 갱신
    public void updateLastMessage(String message) {
        this.lastMessage = message;
        this.lastMessageAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }

    public void deleteByCompany() {
        this.isDeletedByCompany = true;
    }

    public void deleteByStudent() {
        this.isDeletedByStudent = true;
    }

    @Column(name = "project_title", length = 100, nullable = false)
    private String projectTitle;
}
