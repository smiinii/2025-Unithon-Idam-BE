package com.team7.Idam.domain.chat.service;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.entity.ChatMessage;
import com.team7.Idam.domain.chat.entity.ChatRoom;
import com.team7.Idam.domain.chat.repository.ChatMessageRepository;
import com.team7.Idam.domain.chat.repository.ChatRoomRepository;
import com.team7.Idam.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 1. 메시지 전송 (DTO 반환)
    public ChatMessageResponseDto sendMessage(Long roomId, User sender, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        if (!room.getCompany().equals(sender) && !room.getStudent().equals(sender)) {
            throw new SecurityException("채팅방에 참여한 사용자만 메시지를 보낼 수 있습니다.");
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(room)
                .sender(sender)
                .content(content)
                .build();

        // 마지막 메시지 업데이트
        room.updateLastMessage(content);
        ChatMessage savedMessage = chatMessageRepository.save(message);
        return ChatMessageResponseDto.from(savedMessage);
    }

    // 2. 메시지 목록 조회 (DTO 반환)
    public List<ChatMessageResponseDto> getMessagesByRoom(Long roomId, User user) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        if (!room.getCompany().equals(user) && !room.getStudent().equals(user)) {
            throw new SecurityException("이 채팅방에 접근할 수 없습니다.");
        }

        return chatMessageRepository.findByChatRoomOrderBySentAtAsc(room).stream()
                .map(ChatMessageResponseDto::from)
                .collect(Collectors.toList());
    }

    // 3. 메시지 읽음 처리
    @Transactional
    public void markMessagesAsRead(Long roomId, User reader) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        chatMessageRepository.findByChatRoomOrderBySentAtAsc(room).stream()
                .filter(m -> !m.getSender().equals(reader) && !m.isRead())
                .forEach(ChatMessage::markAsRead);
    }
}
