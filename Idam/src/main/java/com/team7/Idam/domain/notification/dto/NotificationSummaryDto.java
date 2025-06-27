package com.team7.Idam.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSummaryDto {
    private Long chatRoomId;
    private String projectTitle;
    private String otherUserName;
    private String profileImageUrl;
    private String lastMessage;       // 가장 최근 메시지
    private LocalDateTime lastMessageAt;
    private int unreadCount;
}
