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
        // ⚠️ 보안 핵심: senderId는 클라이언트에서 보내지 않고, 서버에서 꺼낸다.
        Long senderId = Long.valueOf(principal.getName());

        User sender = userService.getUserById(senderId);
        ChatMessageResponseDto savedMessage = chatMessageService.sendMessage(dto.getRoomId(), sender, dto.getContent());

        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), savedMessage);
    }

}
