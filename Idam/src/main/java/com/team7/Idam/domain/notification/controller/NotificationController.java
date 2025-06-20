package com.team7.Idam.domain.notification.controller;

import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatRoomRepository;
import com.team7.Idam.domain.notification.dto.NotificationGroupDto;
import com.team7.Idam.domain.notification.dto.NotificationMessageDto;
import com.team7.Idam.domain.notification.service.NotificationService;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.repository.UserRepository;
import com.team7.Idam.global.dto.ApiResponse;
import com.team7.Idam.jwt.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // ✅ 1. 알림 온 채팅방 목록 조회
    @GetMapping("/room")
    public ResponseEntity<ApiResponse<List<NotificationGroupDto>>> getNotificationRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<NotificationGroupDto> result = notificationService.getGroupedNotifications(user);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ✅ 2. 특정 채팅방의 알림 메시지 목록 조회
    @GetMapping("/room/{chatRoomId}")
    public ResponseEntity<ApiResponse<List<NotificationMessageDto>>> getNotificationMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long chatRoomId) {

        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatRoom room = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        List<NotificationMessageDto> result = notificationService.getNotificationsForRoom(user, room);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    // ✅ 3. 특정 알림 읽음 처리
    @PatchMapping("/{notification_id}/read")
    public ResponseEntity<ApiResponse<String>> readNotification(
            @PathVariable("notification_id") Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("읽음 처리 완료"));
    }

    // ✅ 4. 전체 알림 읽음 처리
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> readAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        notificationService.markAllAsRead(user);
        return ResponseEntity.ok(ApiResponse.success("전체 읽음 처리 완료"));
    }
}
