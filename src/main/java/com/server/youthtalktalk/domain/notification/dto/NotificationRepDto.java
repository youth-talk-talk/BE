package com.server.youthtalktalk.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.server.youthtalktalk.domain.notification.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public record NotificationRepDto(
        Long notificationId, // 알림 아이디
        NotificationDetail detail, // 알림 종류
        String sender, // 다른 사용자
        Long postId, // 게시글 아이디(게시글 알림 시)
        Long policyId, // 정책 아이디(정책 알림 시)
        String title, // 제목
        String message, // 내용
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt, // 생성일
        boolean isCheck, // 알림 확인 여부
        boolean isRecent // 최근 알림인지 여부(아닐 경우 지난 알림)
) {
    public static NotificationRepDto toDto(Notification notification){
        return new NotificationRepDto(
                notification.getId(),
                notification.getDetail(),
                notification.getSender(),
                notification.getPostId(),
                notification.getPolicyId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isCheck(),
                isRecent(notification)
        );
    }

    private static boolean isRecent(Notification notification) {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        // 최근 알림 기준 : 확인하지 않았으면서, 7일이 지나지 않은 알림
        return !notification.isCheck() && notification.getCreatedAt().isAfter(sevenDaysAgo);
    }

}
