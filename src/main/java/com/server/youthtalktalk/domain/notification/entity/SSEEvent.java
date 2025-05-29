package com.server.youthtalktalk.domain.notification.entity;

import com.server.youthtalktalk.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class SSEEvent {
    private NotificationDetail notificationDetail; // 알림 내용
    private Member receiver; // 수신자
    private String sender; // 송신자
    private String policyTitle; // 정책 제목
    private NotificationType type; // 알림 타입
    private Long id; // 정책 또는 게시글 아이디
    private String comment; // 댓글 내용

    /**
     * 단일 수신자용 생성자
     */
    @Builder
    public SSEEvent(
            NotificationDetail detail,
            Member receiver,
            String sender,
            String policyTitle,
            NotificationType type,
            Long id,
            String comment
    ) {
        this.notificationDetail = detail;
        this.receiver = receiver;
        this.sender = sender;
        this.policyTitle = policyTitle;
        this.type = type;
        this.id = id;
        this.comment = comment;
    }
}
