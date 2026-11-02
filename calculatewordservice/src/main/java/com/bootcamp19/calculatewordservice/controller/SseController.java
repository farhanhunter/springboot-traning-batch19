package com.bootcamp19.calculatewordservice.controller;

import com.bootcamp19.calculatewordservice.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Server-Sent Events: GET /sse/subscribe/huruf-event
 * Client menerima update trending huruf secara realtime setiap ada analisis baru.
 */
@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    @GetMapping("/subscribe/{eventKey}")
    public SseEmitter subscribe(@PathVariable String eventKey) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // timeout 1 jam
        sseService.addEmitter(eventKey, emitter);
        return emitter;
    }
}
