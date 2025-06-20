package com.team7.Idam.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationGroupDto {

    private Long chatRoomId;
    private String projectTitle;
    private String otherUserName;
    private String profileImageUrl;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private int unreadCount;
}
