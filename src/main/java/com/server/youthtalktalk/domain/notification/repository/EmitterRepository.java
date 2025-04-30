package com.server.youthtalktalk.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    public SseEmitter save(String emitterId, SseEmitter sseEmitter);
    public void saveEventCache(String emitterId, Object event);
    public Map<String,SseEmitter> findAllEmitterStartWithByUserId(String userId);
    public Map<String,Object> findAllEventCacheStartWithByUserId(String userId);
    public void deleteById(String emitterId);
    public void deleteAllEmitterStartWithUserId(String userId);
    public void deleteAllEventCacheStartWithUserId(String userId);

}
