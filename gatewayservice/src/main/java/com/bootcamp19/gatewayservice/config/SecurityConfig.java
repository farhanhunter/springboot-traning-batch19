package com.bootcamp19.gatewayservice.config;

import com.bootcamp19.gatewayservice.dto.constant.ConstantVariable;
import com.bootcamp19.gatewayservice.util.ResponseUtil;
import com.bootcamp19.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Gateway berbasis WebFlux, jadi yang dipakai:
 *   SecurityWebFilterChain + ServerHttpSecurity
 * (BUKAN SecurityFilterChain + HttpSecurity milik servlet).
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    @Value("${security.headers.apiKey}")
    private String apiKey;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        RequestHeaderFilter requestHeaderFilter = new RequestHeaderFilter(apiKey);
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // public endpoint (tanpa token)
                        .pathMatchers(ConstantVariable.PUBLIC_ENDPOINT).permitAll()
                        // endpoint lain wajib punya token valid
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, ex) ->
                                ResponseUtil.writeError(exchange, HttpStatus.UNAUTHORIZED,
                                        "Unauthorized or Missing Authorization"))
                )
                // urutan: cek APIKey dulu, baru cek JWT
                .addFilterBefore(requestHeaderFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
