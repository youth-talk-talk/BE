package com.server.youthtalktalk.domain.notification.entity;

import com.server.youthtalktalk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@RequiredArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public class Notification{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationDetail detail; // 알림 종류

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type; // 정책관련인지, 게시글관련인지 구분

    private Long postId; // 게시글 아이디

    private Long policyId; // 정책 아이디

    @Column(length = 255)
    private String title; // 알림 제목

    @Column(length = 255)
    private String message; // 알림 메세지

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver; // 수신자

    private String sender; // 송신자 닉네임

    private boolean isCheck; // 알림 확인 여부

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @Builder(toBuilder = true)
    public Notification(NotificationDetail detail, NotificationType type, Long policyId, Long postId, String title, String message, Member receiver, String sender, boolean isCheck, LocalDateTime createdAt) {
        this.type = type;
        this.detail = detail;
        this.title = title;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.isCheck = isCheck;
        this.createdAt = createdAt;
        this.postId = postId;
        this.policyId = policyId;
    }

    public void setReceiver(Member receiver) {
        this.receiver = receiver;
        if(receiver != null) {
            receiver.getNotifications().add(this);
        }
    }

}
