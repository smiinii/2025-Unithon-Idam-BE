package com.team7.Idam.domain.notification.service;

import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.notification.dto.NotificationGroupDto;
import com.team7.Idam.domain.notification.dto.NotificationMessageDto;
import com.team7.Idam.domain.notification.entity.Notification;
import com.team7.Idam.domain.notification.entity.enums.NotificationType;
import com.team7.Idam.domain.notification.repository.NotificationRepository;
import com.team7.Idam.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // 알림 생성
    public void createNotification(User receiver, ChatRoom room, String messageContent) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .notificationType(NotificationType.CHAT) // enum 타입 맞게 수정
                .message(messageContent)
                .chatRoom(room)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    // 채팅방 목록 + 알림 개수 + 마지막 메시지
    public List<NotificationGroupDto> getGroupedNotifications(User receiver) {

        // 📌 로그: 전달된 유저 ID 확인
        System.out.println("📌 [getGroupedNotifications] receiver ID: " + receiver.getId());
        System.out.println("📌 [getGroupedNotifications] receiver class: " + receiver.getClass());

        List<ChatRoom> rooms = notificationRepository.findChatRoomsWithUnreadNotificationsByReceiver(receiver);
        System.out.println("📌 [getGroupedNotifications] 찾은 채팅방 개수: " + rooms.size());


        return rooms.stream().map(room -> {
            int unreadCount = notificationRepository.findByReceiverAndChatRoomAndIsReadFalseOrderByCreatedAtDesc(receiver, room).size();
            String otherUserName = getOtherUserName(room, receiver);
            String profileImageUrl = getOtherUserProfileImage(room, receiver);

            return new NotificationGroupDto(
                    room.getId(),
                    room.getProjectTitle(),
                    otherUserName,
                    profileImageUrl,
                    room.getLastMessage(),
                    room.getLastMessageAt(),
                    unreadCount
            );
        }).collect(Collectors.toList());
    }

    // 특정 채팅방 알림 메시지 전체 조회
    public List<NotificationMessageDto> getNotificationsForRoom(User receiver, ChatRoom chatRoom) {
        List<Notification> notifications = notificationRepository.findByReceiverAndChatRoomAndIsReadFalseOrderByCreatedAtDesc(receiver, chatRoom);
        String otherUserName = getOtherUserName(chatRoom, receiver);
        String profileImageUrl = getOtherUserProfileImage(chatRoom, receiver);

        return notifications.stream().map(n -> new NotificationMessageDto(
                n.getId(),
                chatRoom.getId(),
                chatRoom.getProjectTitle(),
                otherUserName,
                profileImageUrl,
                n.getMessage(),
                n.getCreatedAt()
        )).collect(Collectors.toList());
    }

    // 알림 읽음 처리
    public void markAsRead(Long notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        n.markAsRead();
    }

    // 전체 알림 읽음 처리
    public void markAllAsRead(User receiver) {
        List<Notification> n = notificationRepository.findByReceiverAndIsReadFalseOrderByCreatedAtDesc(receiver);
        n.forEach(Notification::markAsRead);
    }

    // 상대방 이름 구하기 (채팅방 구조 이용)
    private String getOtherUserName(ChatRoom room, User currentUser) {
        return room.getCompany().equals(currentUser)
                ? room.getStudent().getStudent().getName()
                : room.getCompany().getCompany().getCompanyName();
    }

    // 상대방 프로필 이미지 구하기 (채팅방 구조 이용)
    private String getOtherUserProfileImage(ChatRoom room, User currentUser) {
        return room.getCompany().equals(currentUser)
                ? room.getStudent().getStudent().getProfileImage()
                : room.getCompany().getCompany().getProfileImage();
    }
}
