package com.bootcamp19.gatewayservice.config;

import com.bootcamp19.gatewayservice.dto.constant.ConstantVariable;
import com.bootcamp19.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Lapisan kedua: baca token dari header Authorization: Bearer xxx.
 * Kalau token valid -> set Authentication + teruskan X-User-Id & X-User-Email ke service tujuan.
 * Kalau tidak ada / tidak valid -> biarkan lewat, nanti SecurityConfig yang menolak (401)
 * untuk endpoint yang butuh login.
 *
 * Tidak diberi @Component (alasannya sama dengan RequestHeaderFilter).
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Incoming gateway request: {} {}", request.getMethod(), request.getURI().getPath());

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return chain.filter(exchange);
        }

        String email = jwtUtil.extractEmail(token);
        Long userId = jwtUtil.extractUserId(token);

        // tambahkan header identitas user supaya service di belakang gateway tidak perlu parse JWT lagi
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(ConstantVariable.HEADER_USER_EMAIL, email)
                .header(ConstantVariable.HEADER_USER_ID, String.valueOf(userId))
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                email, null, Collections.emptyList()
        );

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
