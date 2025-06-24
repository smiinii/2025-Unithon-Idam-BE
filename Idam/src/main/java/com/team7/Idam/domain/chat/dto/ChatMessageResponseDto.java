package com.team7.Idam.domain.chat.dto;

import com.team7.Idam.domain.chat.entity.ChatMessage;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.entity.enums.UserType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponseDto {

    private Long messageId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;
    private Long roomId;

    public static ChatMessageResponseDto from(ChatMessage message) {
        User sender = message.getSender();
        String senderName;

        if (sender.getUserType() == UserType.STUDENT) {
            senderName = sender.getStudent().getNickname();
        } else {
            senderName = sender.getCompany().getCompanyName();
        }

        return ChatMessageResponseDto.builder()
                .messageId(message.getId())
                .senderId(sender.getId())
                .roomId(message.getChatRoom().getId())
                .senderName(senderName)
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .isRead(message.isRead())
                .build();
    }
}
