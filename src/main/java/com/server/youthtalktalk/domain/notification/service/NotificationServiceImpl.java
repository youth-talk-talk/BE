package com.server.youthtalktalk.domain.notification.service;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.notification.dto.NotificationListRepDto;
import com.server.youthtalktalk.domain.notification.dto.NotificationRepDto;
import com.server.youthtalktalk.domain.notification.entity.Notification;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import com.server.youthtalktalk.domain.notification.repository.NotificationRepository;
import com.server.youthtalktalk.global.response.exception.notification.NotificationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final NotificationRepository notificationRepository;

    /**
     * 알림 가져오기
     * POST, POLICY 별로 조회
     */
    @Override
    @Transactional
    public NotificationListRepDto getAllNotificationsByType(Member receiver, NotificationType type, Pageable pageable) {
        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        // 일주일 전 날짜
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        // 최근 알림 -> 지난 알림 -> 시간순 정렬한 알림 목록 가져오기(정책별, 커뮤니티별)
        Page<Notification> notifications = notificationRepository.findByReceiver(receiver, type, sevenDaysAgo, pageRequest);

        // NotificationDto로 변환
        return getNotificationListRepDto(notifications);
    }

    /** 알림 확인하기 */
    @Override
    @Transactional
    public void checkNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new);
        notificationRepository.save(notification.toBuilder().isCheck(true).build());
    }

    /** 알림 삭제하기 */
    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(NotificationNotFoundException::new);
        notificationRepository.delete(notification);
    }

    private NotificationListRepDto getNotificationListRepDto(Page<Notification> notifications) {
        // NotificationDto로 변환

        List<NotificationRepDto> responseList = notifications
                .map(NotificationRepDto::toDto)
                .stream()
                .toList();

        return NotificationListRepDto.builder()
                .notifications(responseList)
                .page(notifications.getNumber())
                .total(notifications.getTotalElements())
                .build();
    }


}
