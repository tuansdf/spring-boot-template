package com.example.sbt.config;

import com.example.sbt.common.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(public * com.example.sbt.module..*(..)) || execution(public * com.example.sbt.event..*(..)) || execution(public * com.example.sbt.common.controller..*(..)) || execution(public * com.example.sbt.common.util..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = DateUtils.currentEpochMillis();

        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();

        long key = DateUtils.currentEpochMillis();

        log.info("{} ENTER method: {} with arguments: {}", key, methodName, methodArgs);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("{} Exception in method: {} with message: {}", key, methodName, e.getMessage());
            throw e;
        } finally {
            long exTime = DateUtils.currentEpochMillis() - start;
            log.info("{} EXIT  method: {} after {} ms with result: {}", key, methodName, exTime, result);
        }

        return result;
    }

}
