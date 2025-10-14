package com.example.sbt.infrastructure.web.config;

import com.example.sbt.common.constant.LoggerKey;
import com.example.sbt.infrastructure.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    @Around("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Service *)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();

        log.atInfo()
                .addKeyValue(LoggerKey.EVENT, "ENTER")
                .addKeyValue(LoggerKey.AROUND_KEY, start)
                .addKeyValue(LoggerKey.METHOD_NAME, methodName)
                .log();

        Object result = null;
        try {
            result = joinPoint.proceed();
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.METHOD_NAME, methodName)
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .log();
        } catch (Throwable e) {
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            log.atError()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.METHOD_NAME, methodName)
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .setCause(e instanceof CustomException ? null : e)
                    .log(e.toString());
            throw e;
        }

        return result;
    }
}
