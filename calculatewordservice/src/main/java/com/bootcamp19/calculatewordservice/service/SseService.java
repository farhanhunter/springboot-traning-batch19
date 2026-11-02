package com.bootcamp19.calculatewordservice.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    void addEmitter(String key, SseEmitter emitter);
    void sendEvent(String key, String eventName, Object eventData);
}
