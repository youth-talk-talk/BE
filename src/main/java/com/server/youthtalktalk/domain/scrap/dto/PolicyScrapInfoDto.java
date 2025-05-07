package com.server.youthtalktalk.domain.scrap.dto;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.notification.entity.NotificationDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class PolicyScrapInfoDto{
    private String policyTitle;
    private Long policyId;
    private Member member;
    private LocalDate applyDue;
    private LocalDateTime scrapCratedAt;
}