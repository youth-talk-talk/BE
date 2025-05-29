package com.server.youthtalktalk.domain.notification.service;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.notification.dto.NotificationListRepDto;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationListRepDto getAllNotificationsByType(Member receiver, NotificationType type, Pageable pageable);
    void checkNotification(Long notificationId);
    void deleteNotification(Long notificationId);
}
