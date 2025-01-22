package com.example.springboot.configs;

import com.example.springboot.utils.ConversionUtils;
import com.example.springboot.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(public * com.example.springboot.modules..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] methodArgs = joinPoint.getArgs();

        long start = DateUtils.toEpochMicro(null);
        String key = ConversionUtils.toString(start);

        log.info("{} ENTER method: {} with arguments: {}", key, methodName, methodArgs);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("{} Exception in method: {} with message: {}", key, methodName, e.getMessage());
            throw e;
        } finally {
            long exTime = DateUtils.toEpochMicro(null) - start;
            log.info("{} EXIT  method: {} after {} µs with result: {}", key, methodName, exTime, result);
        }

        return result;
    }

}
