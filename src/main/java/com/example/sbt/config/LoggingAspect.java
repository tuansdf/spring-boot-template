package com.example.sbt.config;

import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.DateUtils;
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

    private static final int MAX_RESULT_LENGTH = 10000;

    @Around("execution(public * com.example.sbt.module..*(..)) || execution(public * com.example.sbt.event..*(..)) || execution(public * com.example.sbt.common.controller..*(..)) || execution(public * com.example.sbt.common.util..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = DateUtils.currentEpochMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.toShortString();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) params.append(", ");
            params.append(paramNames[i]).append("=").append(paramValues[i]);
        }
        String paramString = !params.isEmpty() ? params.toString() : "";

        log.info("[methodAt={}] ENTER [method={}] [arguments={}]", start, methodName, paramString);

        Object result = null;
        try {
            result = joinPoint.proceed();
            long exTime = DateUtils.currentEpochMillis() - start;
            String resultString = ConversionUtils.toString(result);
            if (resultString != null && resultString.length() > MAX_RESULT_LENGTH) {
                resultString = resultString.substring(0, MAX_RESULT_LENGTH);
            }
            log.info("[methodAt={}] EXIT  [method={}] [execTime={} ms] result: {}", start, methodName, exTime, resultString);
        } catch (Throwable e) {
            long exTime = DateUtils.currentEpochMillis() - start;
            log.error("[methodAt={}] EXIT  [method={}] [execTime={} ms] exception: ", start, methodName, exTime, e);
            throw e;
        }

        return result;
    }

}
