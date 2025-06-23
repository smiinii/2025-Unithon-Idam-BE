package com.team7.Idam.domain.chat.controller;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.dto.ChatMessageSocketDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.entity.ChatMessage;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatMessageRepository;
import com.team7.Idam.domain.chat.repository.ChatRoomRepository;
import com.team7.Idam.domain.chat.service.ChatMessageService;
import com.team7.Idam.domain.user.entity.User;
import com.team7.Idam.domain.user.service.UserService;
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

    // 메시지 전송
    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageSocketDto dto, Principal principal) {
        if (principal == null) {
            throw new SecurityException("인증되지 않은 사용자입니다.");
        }

        Long senderId = Long.valueOf(principal.getName());
        User sender = userService.getUserById(senderId);

        // 메시지 저장
        ChatMessageResponseDto savedMessage = chatMessageService.sendMessage(dto.getRoomId(), sender, dto.getContent());

        // 1️⃣ 채팅방 내부 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), savedMessage);

        // 2️⃣ 채팅방 요약정보 전송
        ChatRoom room = chatRoomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User receiver = sender.equals(room.getCompany()) ? room.getStudent() : room.getCompany();

        int unreadCount = (int) room.getMessages().stream()
                .filter(m -> !m.getSender().equals(receiver) && !m.isRead())
                .count();

        ChatMessage lastMessage = chatMessageRepository.findTopByChatRoomOrderBySentAtDesc(room).orElse(null);

        ChatRoomResponseDto summary = ChatRoomResponseDto.from(room, receiver, unreadCount, lastMessage);

        System.out.println("📤 [요약 전송] 수신자 ID: " + receiver.getId());
        System.out.println("📤 [요약 전송] unreadCount: " + unreadCount);
        System.out.println("📤 [요약 전송] 마지막 메시지: " + (lastMessage != null ? lastMessage.getContent() : "없음"));

        messagingTemplate.convertAndSend("/sub/chat/summary/" + receiver.getId(), summary);
    }

    @MessageMapping("/chat/read")
    public void markAsRead(@Payload Long roomId, Principal principal) {
        if (principal == null) return;

        Long readerId = Long.valueOf(principal.getName());
        User reader = userService.getUserById(readerId);

        chatMessageService.markMessagesAsRead(roomId, reader);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User opponent = room.getCompany().getId().equals(readerId)
                ? room.getStudent()
                : room.getCompany();

        System.out.println("📥 [읽음 처리] 읽은 사람 ID: " + readerId);
        System.out.println("📥 [읽음 처리] 상대방(opponent) ID: " + opponent.getId());
        System.out.println("📥 [읽음 처리] 전송 경로: /sub/chat/read/" + roomId + "/" + opponent.getId());

        messagingTemplate.convertAndSend(
                "/sub/chat/read/" + roomId + "/" + opponent.getId(),
                "read"
        );
    }
}
