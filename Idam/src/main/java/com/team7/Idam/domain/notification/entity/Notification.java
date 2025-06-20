package com.team7.Idam.domain.notification.entity;

import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.notification.entity.enums.NotificationType;
import com.team7.Idam.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 받을 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType notificationType;

    // 알림 메시지
    @Column(length = 255, nullable = false)
    private String message;

    // 연결된 채팅방 (이 알림 클릭 시 이동할 곳)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    // 읽음 여부
    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    // 읽음 처리
    public void markAsRead() {
        this.isRead = true;
    }

    // 생성 시각
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
