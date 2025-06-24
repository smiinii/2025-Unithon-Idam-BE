package com.team7.Idam.domain.chat.controller;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.dto.ChatMessageSocketDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatMessageRepository;
import com.team7.Idam.domain.chat.repository.ChatRoomRepository;
import com.team7.Idam.domain.chat.service.ChatMessageService;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    @Transactional
    public void send(@Payload ChatMessageSocketDto dto, Principal principal) {
        if (principal == null) throw new SecurityException("인증되지 않은 사용자입니다.");

        Long senderId = Long.valueOf(principal.getName());
        User sender = userService.getUserById(senderId);

        // 💬 메시지 저장 후 DTO 반환
        ChatMessageResponseDto messageDto = chatMessageService.sendMessage(dto.getRoomId(), sender, dto.getContent());

        // 채팅방 정보 조회 (메시지 포함된 fetch join 사용)
        ChatRoom chatRoom = chatRoomRepository.findWithMessagesById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        // 수신자 식별
        User receiver = sender.equals(chatRoom.getCompany()) ? chatRoom.getStudent() : chatRoom.getCompany();

        // ✅ 여기에 로그 삽입
        System.out.println("🧪 메시지 DTO: " + messageDto);
        System.out.println("🧪 채팅방: " + chatRoom);
        System.out.println("🧪 수신자: " + receiver);

        // 읽지 않은 메시지 수 계산
        long unreadCount = chatRoom.getMessages().stream()
                .filter(m -> !m.getSender().equals(receiver) && !m.isRead())
                .count();

        // 1️⃣ 실시간 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), messageDto);

        // 2️⃣ 실시간 요약 정보 전송
        ChatRoomResponseDto summary = ChatRoomResponseDto.from(chatRoom, receiver, (int) unreadCount, messageDto);
        messagingTemplate.convertAndSend("/sub/chat/summary/" + receiver.getId(), summary);

        System.out.printf("📤 [요약 전송] 수신자 ID: %d, unreadCount: %d, 마지막 메시지: %s%n",
                receiver.getId(), unreadCount, messageDto.getContent());
    }

    @MessageMapping("/chat/read")
    public void markAsRead(@Payload Long roomId, Principal principal) {
        if (principal == null) return;

        Long readerId = Long.valueOf(principal.getName());
        User reader = userService.getUserById(readerId);

        chatMessageService.markMessagesAsRead(roomId, reader);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User opponent = room.getCompany().getId().equals(readerId) ? room.getStudent() : room.getCompany();

        // 3️⃣ 읽음 정보 요약으로 전송
        ChatRoomResponseDto updatedSummary = ChatRoomResponseDto.from(room, opponent, 0, null);
        messagingTemplate.convertAndSend("/sub/chat/summary/" + opponent.getId(), updatedSummary);

        System.out.printf("📥 [읽음 처리 + 요약 전송] 읽은 사람 ID: %d, 상대방 ID: %d%n", readerId, opponent.getId());
    }
}
