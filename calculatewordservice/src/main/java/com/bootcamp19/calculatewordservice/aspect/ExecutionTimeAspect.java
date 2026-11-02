package com.bootcamp19.calculatewordservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @Around: membungkus method target -> bisa jalan sebelum DAN sesudahnya.
 * Coba panggil GET /analyses/{id} dua kali: yang kedua jauh lebih cepat (cache hit).
 */
@Slf4j
@Aspect
@Component
public class ExecutionTimeAspect {

    @Around("execution(* com.bootcamp19.calculatewordservice.controller.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            log.info("{}.{} selesai dalam {}ms",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    System.currentTimeMillis() - start);
        }
    }
}
