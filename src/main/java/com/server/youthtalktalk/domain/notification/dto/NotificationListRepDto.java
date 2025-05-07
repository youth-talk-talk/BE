package com.server.youthtalktalk.domain.notification.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NotificationListRepDto(
        int page,
        Long total,
        List<NotificationRepDto> notifications
) {
}
