package com.team7.Idam.domain.chat.controller;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.dto.ChatMessageSocketDto;
import com.team7.Idam.domain.chat.dto.ChatRoomResponseDto;
import com.team7.Idam.domain.chat.dto.MarkAsReadRequestDto;
import com.team7.Idam.domain.chat.entity.ChatRoom;
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
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    @Transactional
    public void send(@Payload ChatMessageSocketDto dto, Principal principal) {
        if (principal == null) throw new SecurityException("인증되지 않은 사용자입니다.");

        Long senderId = Long.valueOf(principal.getName());
        User sender = userService.getUserById(senderId);

        ChatMessageResponseDto messageDto = chatMessageService.sendMessage(dto.getRoomId(), sender, dto.getContent());

        ChatRoom chatRoom = chatRoomRepository.findWithMessagesById(dto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User receiver = sender.equals(chatRoom.getCompany()) ? chatRoom.getStudent() : chatRoom.getCompany();

        long unreadCount = chatRoom.getMessages().stream()
                .filter(m -> !m.getSender().equals(receiver) && !m.isRead())
                .count();

        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), messageDto);
        ChatRoomResponseDto summary = ChatRoomResponseDto.from(chatRoom, receiver, (int) unreadCount, messageDto);
        messagingTemplate.convertAndSend("/sub/chat/summary/" + receiver.getId(), summary);
    }

    @MessageMapping("/chat/read")
    public void markAsRead(@Payload MarkAsReadRequestDto request, Principal principal) {
        if (principal == null) return;

        Long readerId = Long.valueOf(principal.getName());
        Long roomId = request.getRoomId();

        User reader = userService.getUserById(readerId);
        chatMessageService.markMessagesAsRead(roomId, reader);

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        User opponent = room.getCompany().getId().equals(readerId) ? room.getStudent() : room.getCompany();

        ChatRoomResponseDto updatedSummary = ChatRoomResponseDto.from(room, opponent, 0, null);
        messagingTemplate.convertAndSend("/sub/chat/summary/" + opponent.getId(), updatedSummary);
        messagingTemplate.convertAndSend("/sub/chat/read/" + roomId + "/" + opponent.getId(), "read");
    }
}
