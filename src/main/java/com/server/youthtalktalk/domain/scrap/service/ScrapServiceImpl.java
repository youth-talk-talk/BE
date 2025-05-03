package com.server.youthtalktalk.domain.scrap.service;

import com.server.youthtalktalk.domain.notification.entity.NotificationDetail;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import com.server.youthtalktalk.domain.notification.entity.SSEEvent;
import com.server.youthtalktalk.domain.scrap.dto.PolicyScrapInfoDto;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {
    private final ScrapRepository scrapRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Scheduled(cron = "0 0 9 * * ?")  // 매일 9시 정각에 실행
    //@Scheduled(cron = "0 * * * * *")  // 매분 0초에 실행(테스트용)
    @Override
    @Transactional
    public void sendScrapedPolicyNotification() {
        List<PolicyScrapInfoDto> infoList = scrapRepository.findRecentByDeadlineOrScrapDate();
        for (PolicyScrapInfoDto info : infoList) {
            SSEEvent event = SSEEvent.builder()
                    .id(info.getPolicyId())
                    .type(NotificationType.POLICY)
                    .detail(getNotificationDetail(info.getApplyDue(), info.getScrapCratedAt()))
                    .comment(null)
                    .receiver(info.getMember())
                    .sender(null)
                    .policyTitle(info.getPolicyTitle())
                    .build();
            applicationEventPublisher.publishEvent(event);
        }
    }

    private NotificationDetail getNotificationDetail(LocalDate applyDue, LocalDateTime scrapCreatedAt) {
        // 오늘 마감일인 경우
        if (applyDue != null && applyDue.equals(LocalDate.now())) {
            return NotificationDetail.TODAY_FINISHED;
        }

        // 마감일이 일주일 후인 경우
        else if (applyDue != null && applyDue.equals(LocalDate.now().plusDays(7))) {
            return NotificationDetail.WEEK_BEFORE_FINISHED;
        }

        // 스크랩한 지 일주일이 지난 경우
        else if (scrapCreatedAt.isBefore(LocalDateTime.now().minusDays(7))) {
            return NotificationDetail.WEEK_AFTER_SCRAP;
        }

        // 기본값
        return NotificationDetail.TODAY_FINISHED;
    }
}
