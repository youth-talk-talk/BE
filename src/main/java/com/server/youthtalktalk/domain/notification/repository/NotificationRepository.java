package com.server.youthtalktalk.domain.notification.repository;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.notification.entity.Notification;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /** 모든 정책 가져오기 (최근 알림 : 확인하지 않고 7일이 지나지 않은 알림, 지난 알림 : 이외, 시간순 정렬)*/
    @Query("""
    SELECT n FROM Notification n WHERE n.receiver = :receiver AND n.type = :type
    ORDER BY
        CASE
            WHEN n.isCheck = false AND n.createdAt > :sevenDaysAgo THEN 0
            ELSE 1
        END ASC,
        n.createdAt DESC
    """)
    Page<Notification> findByReceiver(Member receiver, NotificationType type, LocalDateTime sevenDaysAgo, Pageable pageable);

    void deleteAllByReceiverId(Long userId);

}
