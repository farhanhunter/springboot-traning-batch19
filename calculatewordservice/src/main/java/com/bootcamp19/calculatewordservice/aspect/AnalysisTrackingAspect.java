package com.bootcamp19.calculatewordservice.aspect;

import com.bootcamp19.calculatewordservice.dto.response.ResAnalysisDto;
import com.bootcamp19.calculatewordservice.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @AfterReturning: jalan SETELAH method target sukses (tidak jalan kalau method melempar exception).
 * Setiap analisis baru tersimpan -> kirim kalimatnya ke Kafka topic WORD_ANALYZED.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AnalysisTrackingAspect {
    private final KafkaProducer kafkaProducer;

    @Value("${kafka.topic.word-analyzed}")
    private String topic;

    @AfterReturning(
            pointcut = "execution(* com.bootcamp19.calculatewordservice.service.impl.AnalysisServiceImpl.createAnalysis(..))"
                    + " || execution(* com.bootcamp19.calculatewordservice.service.impl.AnalysisServiceImpl.analyzeRandomQuote(..))",
            returning = "result"
    )
    public void trackAnalysis(JoinPoint joinPoint, Object result) {
        if (result instanceof ResAnalysisDto analysis) {
            kafkaProducer.sendMessage(topic, analysis.getKalimat());
        }
    }
}
