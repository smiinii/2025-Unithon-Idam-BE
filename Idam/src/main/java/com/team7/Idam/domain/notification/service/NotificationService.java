package com.team7.Idam.domain.notification.service;

import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.notification.dto.NotificationGroupDto;
import com.team7.Idam.domain.notification.dto.NotificationMessageDto;
import com.team7.Idam.domain.notification.entity.Notification;
import com.team7.Idam.domain.notification.entity.enums.NotificationType;
import com.team7.Idam.domain.notification.repository.NotificationRepository;
import com.team7.Idam.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.team7.Idam.domain.notification.dto.NotificationSummaryDto;
import com.team7.Idam.domain.notification.dto.NotificationSocketResponseDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 알림 생성
    public void createAndSend(User receiver, ChatRoom room, String messageContent, NotificationType type) {
        // 1. 저장
        Notification notification = Notification.builder()
                .receiver(receiver)
                .notificationType(type)
                .message(messageContent)
                .chatRoom(room)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // 2. summary 계산
        int unreadCount = notificationRepository
                .findByReceiverAndChatRoomAndIsReadFalseOrderByCreatedAtDesc(receiver, room)
                .size();

        String otherUserName = getOtherUserName(room, receiver);
        String profileImageUrl = getOtherUserProfileImage(room, receiver);

        // 3. DTO 생성
        NotificationMessageDto messageDto = NotificationMessageDto.builder()
                .notificationId(notification.getId())
                .chatRoomId(room.getId())
                .projectTitle(room.getProjectTitle())
                .otherUserName(otherUserName)
                .profileImageUrl(profileImageUrl)
                .message(messageContent)
                .createdAt(notification.getCreatedAt())
                .type(type.name())
                .build();

        NotificationSummaryDto summaryDto = NotificationSummaryDto.builder()
                .chatRoomId(room.getId())
                .projectTitle(room.getProjectTitle())
                .otherUserName(otherUserName)
                .profileImageUrl(profileImageUrl)
                .lastMessage(messageContent)
                .lastMessageAt(notification.getCreatedAt())
                .unreadCount(unreadCount)
                .build();

        // 4. WebSocket 전송
        NotificationSocketResponseDto response = NotificationSocketResponseDto.builder()
                .notification(messageDto)
                .summary(summaryDto)
                .build();

        messagingTemplate.convertAndSend("/sub/notifications/" + receiver.getId(), response);
    }

    public List<NotificationMessageDto> getNotificationsForRoom(User receiver, ChatRoom chatRoom) {
        List<Notification> notifications = notificationRepository
                .findByReceiverAndChatRoomAndIsReadFalseOrderByCreatedAtDesc(receiver, chatRoom);

        String otherUserName = getOtherUserName(chatRoom, receiver);
        String profileImageUrl = getOtherUserProfileImage(chatRoom, receiver);

        return notifications.stream().map(n -> new NotificationMessageDto(
                n.getId(),
                chatRoom.getId(),
                chatRoom.getProjectTitle(),
                otherUserName,
                profileImageUrl,
                n.getMessage(),
                n.getCreatedAt(),
                n.getNotificationType().name()
        )).collect(Collectors.toList());
    }

    // 특정 채팅방 알림 메시지 전체 조회
    public List<NotificationGroupDto> getGroupedNotifications(User user) {
        List<Notification> notifications = notificationRepository.findByReceiverAndIsReadFalse(user);

        if (notifications.isEmpty()) {
            return List.of(); // ✅ 알림 없으면 빈 리스트 반환
        }

        return notifications.stream()
                .filter(n -> n.getChatRoom() != null) // ✅ chatRoom null 방지
                .collect(Collectors.groupingBy(Notification::getChatRoom))
                .entrySet()
                .stream()
                .map(entry -> {
                    ChatRoom room = entry.getKey();
                    List<Notification> roomNotifications = entry.getValue();

                    Notification latest = roomNotifications.stream()
                            .filter(n -> n.getCreatedAt() != null)
                            .max(Comparator.comparing(Notification::getCreatedAt))
                            .orElse(null); // ✅ null-safe

                    if (latest == null) return null; // 건너뛰기

                    String otherUserName = getOtherUserName(room, user);
                    String profileImageUrl = getOtherUserProfileImage(room, user);

                    return new NotificationGroupDto(
                            room.getId(),
                            room.getProjectTitle(),
                            otherUserName,
                            profileImageUrl,
                            latest.getMessage(),
                            latest.getCreatedAt(),
                            roomNotifications.size()
                    );
                })
                .filter(dto -> dto != null) // ✅ null 방지
                .collect(Collectors.toList());
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

    @Transactional
    public void markAsReadByRoom(User user, ChatRoom room) {
        List<Notification> notifications = notificationRepository
                .findByReceiverAndChatRoomAndIsReadFalse(user, room);

        for (Notification n : notifications) {
            n.markAsRead(); // 알림 엔티티의 읽음 처리 메서드
        }
    }

    // 상대방 이름 구하기 (채팅방 구조 이용)
    private String getOtherUserName(ChatRoom room, User currentUser) {
        if (room.getCompany() == null || room.getStudent() == null)
            return "알 수 없음";

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
