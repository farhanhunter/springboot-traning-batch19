package com.bootcamp19.userservice.aspect;

import com.bootcamp19.userservice.config.filter.servlet.CachedBodyHttpServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP:
 *   Aspect   = class ini
 *   Advice   = method logController (@Around: sebelum DAN sesudah method target)
 *   Pointcut = execution(* ...controller.*.*(..)) -> semua method di package controller
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.bootcamp19.userservice.controller.*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long start = System.currentTimeMillis();
        log.info("Incoming Request -> [{}] {} | {}.{}", method, uri, className, methodName);

        if (request instanceof CachedBodyHttpServletRequest wrapper) {
            // sensor password supaya tidak tercetak di log
            String body = wrapper.getContentAsString()
                    .replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"");
            log.info("Request Body: {}", body);
        }

        try {
            Object result = joinPoint.proceed(); // jalankan method controller aslinya
            log.info("Outgoing Response -> [{}] {} | Success in {}ms", method, uri, System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            log.error("Outgoing Error -> [{}] {} | Failed: {} | {}ms", method, uri, e.getMessage(), System.currentTimeMillis() - start);
            throw e;
        }
    }
}
