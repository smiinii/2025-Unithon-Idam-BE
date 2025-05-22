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

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatMessageService chatMessageService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    // /pub/chat/send로 메시지가 전송되면 실행
    @MessageMapping("/chat/send") // STOMP 프로토콜에서 클라이언트가 보내는 목적지 주소 매핑
    public void send(@Payload ChatMessageSocketDto dto) { // @Payload : STOMP 메시지의 본문 데이터를 자동으로 매핑해주는 역할
        User sender = userService.getUserById(dto.getSenderId());
        ChatMessageResponseDto savedMessage = chatMessageService.sendMessage(dto.getRoomId(), sender, dto.getContent());

        // 프론트가 구독 중인 대상에게 실시간 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getRoomId(), savedMessage);
    }

    // nginx에서 proxcy 설정하기 꼭!!!

}
