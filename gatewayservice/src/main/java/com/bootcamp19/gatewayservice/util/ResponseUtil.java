package com.bootcamp19.gatewayservice.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Menulis response error dengan format Base Response yang sama
 * dengan service lain: { reqId, status, message, data }.
 * Gateway itu reactive (WebFlux), jadi response ditulis lewat DataBuffer.
 */
public class ResponseUtil {
    private ResponseUtil() {}

    public static Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = "{"
                + "\"reqId\":\"" + UUID.randomUUID() + "\","
                + "\"status\":\"F\","
                + "\"message\":\"" + message.replace("\"", "'") + "\","
                + "\"data\":null"
                + "}";

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
