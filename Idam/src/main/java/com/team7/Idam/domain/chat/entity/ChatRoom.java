package com.team7.Idam.domain.chat.entity;

import com.team7.Idam.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private boolean isDeletedByCompany = false;

    // 삭제 여부 (soft delete)
    @Column(name = "is_deleted_by_student", nullable = false)
    private boolean isDeletedByStudent = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // 마지막 메시지 갱신
    public void updateLastMessage(String message) {
        this.lastMessage = message;
        this.lastMessageAt = LocalDateTime.now();
    }

    public void deleteByCompany() {
        this.isDeletedByCompany = true;
    }

    public void deleteByStudent() {
        this.isDeletedByStudent = true;
    }
}
