package com.server.youthtalktalk.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationListResponse {
    private int pageNum;
    private int pageSize;
    private Long totalCnt;
    private List<NotificationResponse> notifications;
}
