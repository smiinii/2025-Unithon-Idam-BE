package com.team7.Idam.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageSocketDto {
    private Long roomId;
    private Long senderId;
    private String content;
}