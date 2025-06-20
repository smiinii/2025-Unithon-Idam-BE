package com.team7.Idam.domain.notification.repository;

import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.notification.entity.Notification;
import com.team7.Idam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // ✅ [1] 특정 유저에게 안 읽은 알림이 존재하는 채팅방 목록 (중복 없이, 최신 알림 순)
    @Query("""
        SELECT n.chatRoom
        FROM Notification n
        WHERE n.receiver = :receiver
          AND n.isRead = false
        GROUP BY n.chatRoom
        ORDER BY MAX(n.createdAt) DESC
    """)
    List<ChatRoom> findChatRoomsWithUnreadNotificationsByReceiver(User receiver);

    // ✅ [2] 특정 채팅방에서 해당 유저가 받은 안 읽은 알림 목록 (메시지 펼치기 용)
    List<Notification> findByReceiverAndChatRoomAndIsReadFalseOrderByCreatedAtDesc(User receiver, ChatRoom chatRoom);

    // ✅ [3] (선택) 특정 유저의 전체 안 읽은 알림 (UI 배지 등에 사용 가능)
    List<Notification> findByReceiverAndIsReadFalseOrderByCreatedAtDesc(User receiver);
}
