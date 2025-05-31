package com.server.youthtalktalk.domain.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.youthtalktalk.domain.member.entity.Member;
import com.server.youthtalktalk.domain.notification.dto.NotificationRepDto;
import com.server.youthtalktalk.domain.notification.entity.Notification;
import com.server.youthtalktalk.domain.notification.entity.SSEEvent;
import com.server.youthtalktalk.domain.notification.repository.EmitterRepository;
import com.server.youthtalktalk.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SSEService {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;
    private final EmitterRepository emitterRepository;
    private final MessageSource messageSource;

    // 연결 지속시간 한 시간
    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000;

    public SseEmitter subscribe(Member user, String lastEventId) {
        // 고유 아이디 생성
        String emitterId = user.getId() + "_" + System.currentTimeMillis();

        // 기존 연결이 있다면 정리
        emitterRepository.deleteAllEmitterStartWithUserId(String.valueOf(user.getId()));
        emitterRepository.deleteAllEventCacheStartWithUserId(String.valueOf(user.getId()));

        SseEmitter sseEmitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("new emitter added : {}", emitterId);
        log.info("lastEventId : {}", lastEventId);

        /* 상황별 emitter 삭제 처리 */
        sseEmitter.onCompletion(() -> {
            log.info("SSE connection completed: {}", emitterId);
            emitterRepository.deleteById(emitterId);
        });
        sseEmitter.onTimeout(() -> {
            log.info("SSE connection timeout: {}", emitterId);
            emitterRepository.deleteById(emitterId);
        });
        sseEmitter.onError((e) -> {
            log.error("SSE connection error: {} - {}", emitterId, e.getMessage());
            emitterRepository.deleteById(emitterId);
        });

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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void send(SSEEvent event) {
        Notification notification = notificationService.saveNotification(event);
        saveEventToUserEmitters(notification.getReceiver().getId(), notification);
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
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(emitterId)
                    .data(data));
        } catch (IOException e) {
            // 연결이 끊어진 상태라면 해당 emitter만 지우고 넘어간다
            emitterRepository.deleteById(emitterId);
            // 필요하면 로그만 남기고 종료
            log.warn("SSE Connection Lost: emitterId={} 삭제", emitterId);
            return;
        }
    }
}
