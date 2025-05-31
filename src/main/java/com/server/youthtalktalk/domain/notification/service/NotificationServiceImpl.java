package com.server.youthtalktalk.domain.notification.service;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.repository.MemberRepository;
import com.server.youthtalktalk.domain.notification.dto.NotificationListRepDto;
import com.server.youthtalktalk.domain.notification.dto.NotificationRepDto;
import com.server.youthtalktalk.domain.notification.entity.Notification;
import com.server.youthtalktalk.domain.notification.entity.NotificationDetail;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import com.server.youthtalktalk.domain.notification.entity.SSEEvent;
import com.server.youthtalktalk.domain.notification.repository.NotificationRepository;
import com.server.youthtalktalk.global.response.exception.notification.NotificationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
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
    private final MessageSource messageSource;

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

    @Override
    @Transactional
    public Notification saveNotification(SSEEvent sseEvent) {
        // 알램 객체 생성
        NotificationType type = sseEvent.getType();
        Long postId = type.equals(NotificationType.POST) ? sseEvent.getId() : null;
        Long policyId = type.equals(NotificationType.POLICY) ? sseEvent.getId() : null;

        String[] messages = setTitleAndContent(sseEvent.getComment(), sseEvent.getPolicyTitle(), sseEvent.getSender(), sseEvent.getNotificationDetail());
        String title = messages[0];
        String message = messages[1];

        Notification notification = Notification.builder()
                .sender(sseEvent.getSender())
                .type(type)
                .createdAt(LocalDateTime.now())
                .detail(sseEvent.getNotificationDetail())
                .postId(postId)
                .policyId(policyId)
                .isCheck(false)
                .title(title)
                .message(message)
                .build();
        notification.setReceiver(sseEvent.getReceiver());
        return notificationRepository.save(notification);
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

    private String[] setTitleAndContent(String comment, String policyTitle, String sender, NotificationDetail detail){
        String title = "";
        String message = "";

        switch(detail){
            case POST_COMMENT: message = comment;
            case POST_COMMENT_LIKE:
            case POLICY_COMMENT_LIKE:
                title = messageSource.getMessage("notification." + detail.name() + ".title", new Object[]{sender}, null);
                break;

            case TODAY_FINISHED:
            case WEEK_BEFORE_FINISHED:
            case WEEK_AFTER_SCRAP:
                title = messageSource.getMessage("notification." + detail.name() + ".title", null, null);
                message = messageSource.getMessage("notification." + detail + ".policies", new Object[]{policyTitle}, null);
                break;
        }

        return new String[]{title, message};
    }

}
