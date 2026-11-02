package com.bootcamp19.calculatewordservice.consumer;

import com.bootcamp19.calculatewordservice.service.SseService;
import com.bootcamp19.calculatewordservice.util.CharCounterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Setiap ada kalimat yang dianalisis, consumer ini:
 * 1. menambah skor huruf di Redis Sorted Set (trending huruf global)
 * 2. mengirim event SSE ke client yang sedang subscribe
 *
 * groupId sama = instance consumer berbagi beban (tiap pesan diproses 1 instance).
 * groupId beda = tiap group dapat salinan pesan.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WordAnalyzedConsumer {
    public static final String TRENDING_KEY = "batch19:huruf:trending";

    private final StringRedisTemplate stringRedisTemplate;
    private final SseService sseService;

    @KafkaListener(
            topics = "${kafka.topic.word-analyzed}",
            groupId = "calculateword-trending"
    )
    public void processWordAnalyzed(String kalimat) {
        log.info("Consume WORD_ANALYZED: {}", kalimat);

        Map<String, Integer> frekuensi = CharCounterUtil.hitungFrekuensi(kalimat);
        frekuensi.forEach((huruf, jumlah) ->
                stringRedisTemplate.opsForZSet().incrementScore(TRENDING_KEY, huruf, jumlah)
        );

        sseService.sendEvent("huruf-event", "huruf-trending", frekuensi);
    }
}
