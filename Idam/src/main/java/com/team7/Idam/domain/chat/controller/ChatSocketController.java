package com.team7.Idam.domain.chat.controller;

import com.team7.Idam.domain.chat.dto.ChatMessageResponseDto;
import com.team7.Idam.domain.chat.dto.ChatMessageSocketDto;
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
    private final SimpMessagingTemplate messagingTemplate;

    // /pub/chat/send로 메시지가 전송되면 실행
    @MessageMapping("/chat/send")
    public void send(@Payload ChatMessageSocketDto dto, Principal principal) {
        System.out.println("📩 [WebSocket 수신] 메시지 도착: " + dto);
        System.out.println("🔐 Principal: " + (principal != null ? principal.getName() : "null"));

        if (principal == null) {
            throw new SecurityException("인증되지 않은 사용자입니다. principal이 null입니다.");
        }

        Long senderId;
        try {
            senderId = Long.valueOf(principal.getName());
        } catch (NumberFormatException e) {
            throw new SecurityException("principal name이 숫자가 아님: " + principal.getName());
        }

        System.out.println("✅ 인증된 senderId: " + senderId);

        User sender = userService.getUserById(senderId);
        System.out.println("👤 유저 정보: " + sender.getEmail() + " / " + sender.getUserType());

        ChatMessageResponseDto savedMessage = chatMessageService.sendMessage(dto.getRoomId(), sender, dto.getContent());

        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), savedMessage);
        System.out.println("📤 메시지 전송 완료 → /sub/chat/room/" + dto.getRoomId());
    }
}
