package com.bootcamp19.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // class ini berisi konfigurasi Bean untuk Spring
public class GatewayConfig {

    /**
     * Routing:
     *   /gateway/user/**          -> userservice          (context-path /user)
     *   /gateway/calculateword/** -> calculatewordservice (context-path /calculateword)
     *
     * lb://nama-service = cari alamat service di Eureka, lalu bagi beban antar instance.
     */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("userservice", r -> r
                        .path("/gateway/user/**")
                        .filters(f -> f.rewritePath(
                                "/gateway/user/(?<segment>.*)",
                                "/user/${segment}"
                        ))
                        .uri("lb://userservice"))
                .route("calculatewordservice", r -> r
                        .path("/gateway/calculateword/**")
                        .filters(f -> f.rewritePath(
                                "/gateway/calculateword/(?<segment>.*)",
                                "/calculateword/${segment}"
                        ))
                        .uri("lb://calculatewordservice"))
                .build();
    }
}
