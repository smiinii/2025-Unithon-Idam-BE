package com.team7.Idam.domain.notification.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSocketResponseDto {
    private NotificationMessageDto notification;
    private NotificationSummaryDto summary;
}
