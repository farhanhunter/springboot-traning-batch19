package com.bootcamp19.calculatewordservice.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

/**
 * Koneksi Redis diatur otomatis dari spring.data.redis.* di application.yaml.
 * StringRedisTemplate juga sudah disediakan otomatis oleh Spring Boot.
 */
@Configuration
@EnableCaching // aktifkan @Cacheable / @CacheEvict
public class RedisConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> builder
                // default untuk semua cache
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(5))
                        .computePrefixWith(cacheName -> "batch19:" + cacheName + ":")
                        .disableCachingNullValues())
                // khusus cache "analysis": TTL 2 menit
                .withCacheConfiguration("analysis", RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(2))
                        .computePrefixWith(cacheName -> "batch19:" + cacheName + ":")
                        .disableCachingNullValues());
    }
}
