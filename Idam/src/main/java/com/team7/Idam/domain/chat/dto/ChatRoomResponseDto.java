package com.team7.Idam.domain.chat.dto;

import com.team7.Idam.domain.chat.entity.ChatMessage;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.entity.enums.UserType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponseDto {

    private Long roomId;
    private Long opponentId;
    private String opponentName;
    private String opponentProfileImage;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private String projectTitle;
    private int unreadCount;

    // 가장 기본 형태
    public static ChatRoomResponseDto from(ChatRoom room, User currentUser) {
        return from(room, currentUser, 0, null);
    }

    // unreadCount 포함
    public static ChatRoomResponseDto from(ChatRoom room, User currentUser, int unreadCount) {
        return from(room, currentUser, unreadCount, null);
    }

    // unreadCount와 lastMessage까지 포함 (WebSocket에 적합)
    public static ChatRoomResponseDto from(ChatRoom room, User currentUser, int unreadCount, ChatMessageResponseDto lastMessageDto) {
        User opponent = room.getCompany().equals(currentUser)
                ? room.getStudent()
                : room.getCompany();

        // ✅ 여기에 null 체크 로그 삽입
        if (opponent == null) {
            System.out.println("❌ opponent is null!");
        }
        if (opponent.getUserType() == UserType.STUDENT && opponent.getStudent() == null) {
            System.out.println("❌ opponent.getStudent() is null!");
        }
        if (opponent.getUserType() == UserType.COMPANY && opponent.getCompany() == null) {
            System.out.println("❌ opponent.getCompany() is null!");
        }

        String opponentName = (opponent.getUserType() == UserType.STUDENT && opponent.getStudent() != null)
                ? opponent.getStudent().getNickname()
                : (opponent.getUserType() == UserType.COMPANY && opponent.getCompany() != null)
                ? opponent.getCompany().getCompanyName()
                : "알 수 없음";

        String opponentProfileImage = (opponent.getUserType() == UserType.STUDENT && opponent.getStudent() != null)
                ? opponent.getStudent().getProfileImage()
                : (opponent.getUserType() == UserType.COMPANY && opponent.getCompany() != null)
                ? opponent.getCompany().getProfileImage()
                : null;

        return ChatRoomResponseDto.builder()
                .roomId(room.getId())
                .opponentId(opponent.getId())
                .opponentName(opponentName)
                .opponentProfileImage(opponentProfileImage)
                .lastMessage(lastMessageDto != null ? lastMessageDto.getContent() : room.getLastMessage())
                .lastMessageAt(lastMessageDto != null ? lastMessageDto.getSentAt() : room.getLastMessageAt())
                .projectTitle(room.getProjectTitle())
                .unreadCount(unreadCount)
                .build();
    }
}
