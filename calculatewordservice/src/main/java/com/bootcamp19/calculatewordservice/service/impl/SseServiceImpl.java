package com.bootcamp19.calculatewordservice.service.impl;

import com.bootcamp19.calculatewordservice.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class SseServiceImpl implements SseService {
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    @Override
    public void addEmitter(String key, SseEmitter emitter) {
        List<SseEmitter> sseEmitters = emitters.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>());

        Runnable cleanup = () -> sseEmitters.remove(emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(ex -> cleanup.run());

        try {
            sseEmitters.add(emitter);
            emitter.send(SseEmitter.event()
                    .name("connection-ready")
                    .data("Connected to " + key + " stream"));
            log.info("Client connected to stream: {}", key);
        } catch (IOException e) {
            log.warn("Failed to send initial event for key: {}", key);
            sseEmitters.remove(emitter);
        }
    }

    @Override
    public void sendEvent(String key, String eventName, Object eventData) {
        List<SseEmitter> sseEmitters = emitters.get(key);
        if (sseEmitters == null || sseEmitters.isEmpty()) {
            return;
        }

        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : sseEmitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(eventData));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        sseEmitters.removeAll(deadEmitters);
    }
}
