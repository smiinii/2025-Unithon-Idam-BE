package com.team7.Idam.domain.chat.dto;

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

    // ✅ 기존 메서드는 기본값 0을 주입
    public static ChatRoomResponseDto from(ChatRoom room, User currentUser) {
        return from(room, currentUser, 0);
    }

    // ✅ 새로 추가된 메서드: unreadCount 포함
    public static ChatRoomResponseDto from(ChatRoom room, User currentUser, int unreadCount) {
        User opponent = room.getCompany().equals(currentUser)
                ? room.getStudent()
                : room.getCompany();

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
                .lastMessage(room.getLastMessage())
                .lastMessageAt(room.getLastMessageAt())
                .projectTitle(room.getProjectTitle())
                .unreadCount(unreadCount)
                .build();
    }
}
