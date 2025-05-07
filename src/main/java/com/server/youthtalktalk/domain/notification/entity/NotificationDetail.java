package com.server.youthtalktalk.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationDetail {
    POST_COMMENT("notification.POST_COMMENT", null), // 내 게시글 댓글 알림
    POST_COMMENT_LIKE("notification.POST_COMMENT_LIKE", null), // 게시글에 단 내 댓글 좋아요 알림
    POLICY_COMMENT_LIKE("notification.POLICY_COMMENT_LIKE", null), // 정책에 단 내 댓글 좋아요 알림
    TODAY_FINISHED("notification.TODAY_FINISHED.title", "notification.TODAY_FINISHED.content"), // 당일 마감 스크랩 정책 알림
    WEEK_BEFORE_FINISHED("notification.WEEK_BEFORE_FINISHED.title", "notification.WEEK_BEFORE_FINISHED.content"), // 마감 일주일 전 스크랩 정책 알림
    WEEK_AFTER_SCRAP("notification.WEEK_AFTER_SCRAP.title", "notification.WEEK_AFTER_SCRAP.content"); // 스크랩한지 일주일 초과 정책 알림

    private final String titleKey;
    private final String contentKey;
}
