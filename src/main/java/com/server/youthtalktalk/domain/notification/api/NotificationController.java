package com.server.youthtalktalk.domain.notification.api;

import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.member.service.MemberService;
import com.server.youthtalktalk.domain.notification.dto.NotificationListRepDto;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import com.server.youthtalktalk.domain.notification.service.NotificationService;
import com.server.youthtalktalk.domain.notification.service.SSEService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final SSEService sseService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NotificationService notificationService;
    private final MemberService memberService;

    // Last-Event-ID는 sse 연결이 끊어졌을 때 클라이언트가 받은 마지막 메세지. 항상 존재 x
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return ResponseEntity.ok(sseService.subscribe(memberService.getCurrentMember(), lastEventId));
    }

    /**
     * 알림 조회
     * type : POST, POLICY
     */
    @GetMapping("")
    public ResponseEntity<NotificationListRepDto> getAllPostNotificationInfo(@RequestParam NotificationType type, @PageableDefault(size = 10) Pageable pageable) {
        Member member = memberService.getCurrentMember();
        NotificationListRepDto notificationListResponse = notificationService.getAllNotificationsByType(member, type, pageable);
        return ResponseEntity.ok().body(notificationListResponse);
    }

    /**
     * 알림 확인
     */
    @PatchMapping("/{id}")
    public ResponseEntity<String> checkNotification(@PathVariable("id") Long id){
        notificationService.checkNotification(id);
        return ResponseEntity.ok().body("Checking Successful");
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable("id") Long id){
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().body("Delete Successful");
    }

    @GetMapping("/test")
    public void test() {
    }
}
