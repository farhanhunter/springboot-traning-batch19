package com.bootcamp19.gatewayservice.config;

import com.bootcamp19.gatewayservice.dto.constant.ConstantVariable;
import com.bootcamp19.gatewayservice.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Lapisan pertama: setiap request WAJIB membawa header APIKey yang benar.
 *
 * Sengaja TIDAK diberi @Component: kalau jadi bean, WebFlux otomatis memasang
 * filter ini juga di luar security chain sehingga jalan dua kali.
 * Objeknya dibuat di SecurityConfig.
 */
public class RequestHeaderFilter implements WebFilter {

    private final String apiKey;

    public RequestHeaderFilter(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestApiKey = exchange.getRequest()
                .getHeaders()
                .getFirst(ConstantVariable.HEADER_API_KEY);

        if (!apiKey.equals(requestApiKey)) {
            return ResponseUtil.writeError(exchange, HttpStatus.UNAUTHORIZED,
                    "Unauthorized Invalid Security Header");
        }
        return chain.filter(exchange);
    }
}
