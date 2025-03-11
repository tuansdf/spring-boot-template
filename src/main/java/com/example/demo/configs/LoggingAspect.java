package com.example.demo.configs;

import com.example.demo.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(public * com.example.demo.modules..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();

        long start = System.currentTimeMillis();
        String key = RandomUtils.Insecure.generateTimeBasedUUID().toString();

        log.info("{} ENTER method: {} with arguments: {}", key, methodName, methodArgs);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("{} Exception in method: {} with message: {}", key, methodName, e.getMessage());
            throw e;
        } finally {
            long exTime = System.currentTimeMillis() - start;
            log.info("{} EXIT  method: {} after {} ms with result: {}", key, methodName, exTime, result);
        }

        return result;
    }

}
