package com.server.youthtalktalk.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.notification.dto.NotificationRepDto;
import com.server.youthtalktalk.domain.notification.entity.Notification;
import com.server.youthtalktalk.domain.notification.entity.NotificationDetail;
import com.server.youthtalktalk.domain.notification.entity.NotificationType;
import com.server.youthtalktalk.domain.notification.entity.SSEEvent;
import com.server.youthtalktalk.domain.notification.repository.EmitterRepository;
import com.server.youthtalktalk.domain.notification.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSEService {
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;
    private final MessageSource messageSource;

    // 연결 지속시간 한 시간
    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000;

    public SseEmitter subscribe(Member user, String lastEventId) {
        // 고유 아이디 생성
        String emitterId = user.getId() + "_" + System.currentTimeMillis();

        SseEmitter sseEmitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("new emitter added : {}", sseEmitter);
        log.info("lastEventId : {}", lastEventId);

        /* 상황별 emitter 삭제 처리 */
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(emitterId)); //완료 시, 타임아웃 시, 에러 발생 시
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        sseEmitter.onError((e) -> emitterRepository.deleteById(emitterId));

        /* 503 Service Unavailable 방지용 dummy event 전송 */
        sendToClient(sseEmitter, emitterId, "EventStream Created. [userId=" + user.getId() + "]");

        /* client가 미수신한 event 목록이 존재하는 경우 */
        if(!lastEventId.isEmpty()) { //client가 미수신한 event가 존재하는 경우 이를 전송하여 유실 예방
            Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(user.getId())); //id에 해당하는 eventCache 조회
            eventCaches.entrySet().stream() //미수신 상태인 event 목록 전송
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(sseEmitter, entry.getKey(), entry.getValue()));
        }

        return sseEmitter;
    }

    @EventListener
    public void send(SSEEvent sseEvent) {
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
        Notification savedNotification = notificationRepository.save(notification);

        saveEventToUserEmitters(notification.getReceiver().getId(), savedNotification);
    }


    private void saveEventToUserEmitters(Long receiverId, Notification savedNotification) {
        String userId = String.valueOf(receiverId);
        // 로그인한 유저의 sseEmitter 전체 호출
        Map<String,SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByUserId(userId);
        sseEmitters.forEach(
                (key,emitter)->{
                    emitterRepository.saveEventCache(key, savedNotification);
                    NotificationRepDto response = NotificationRepDto.toDto(savedNotification);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = null;
                    try {
                        jsonData = objectMapper.writeValueAsString(response);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    sendToClient(emitter,key,jsonData);
                }
        );
    }

    private void sendToClient(SseEmitter sseEmitter, String emitterId, Object data) {
        try{
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("SSE Connection Failed : 알림 전송 실패");
        }
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
