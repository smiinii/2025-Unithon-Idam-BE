package com.team7.Idam.domain.chat.repository;

import com.team7.Idam.domain.chat.entity.ChatMessage;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 1. 채팅방의 모든 메시지를 시간순으로 조회 (오름차순)
    List<ChatMessage> findByChatRoomOrderBySentAtAsc(ChatRoom chatRoom);

    // 2. 특정 유저 기준 안 읽은 메시지 개수 집계 (채팅방 별로)
    @Query("""
        SELECT m.chatRoom.id AS roomId, COUNT(m) AS unreadCount
        FROM ChatMessage m
        WHERE m.sender != :user AND m.isRead = false
        GROUP BY m.chatRoom.id
    """)
    List<UnreadCountProjection> findUnreadCountsForUser(@Param("user") User user);

    interface UnreadCountProjection {
        Long getRoomId();
        int getUnreadCount();
    }

    // 3. 여러 채팅방에 대한 마지막 메시지를 효율적으로 조회 (GROUP BY + MAX)
    @Query("""
        SELECT m
        FROM ChatMessage m
        WHERE m.chatRoom IN :rooms AND m.sentAt = (
        SELECT MAX(m2.sentAt)
        FROM ChatMessage m2
        WHERE m2.chatRoom = m.chatRoom
        )
    """)
    List<ChatMessage> findLastMessagesForRooms(@Param("rooms") List<ChatRoom> rooms);
}
