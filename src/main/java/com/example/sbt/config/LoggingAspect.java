package com.example.sbt.config;

import com.example.sbt.core.constant.LoggerKey;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.DateUtils;
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
            "within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Repository *)")
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
        String arguments = !params.isEmpty() ? params.toString() : "";

        log.atInfo()
                .addKeyValue(LoggerKey.EVENT, "ENTER")
                .addKeyValue(LoggerKey.AROUND_KEY, start)
                .addKeyValue(LoggerKey.METHOD, methodName)
                .addKeyValue(LoggerKey.ARGUMENTS, arguments)
                .log();

        Object result = null;
        try {
            result = joinPoint.proceed();
            long elapsedMs = DateUtils.currentEpochMillis() - start;
            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.METHOD, methodName)
                    .addKeyValue(LoggerKey.ARGUMENTS, arguments)
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .log(ConversionUtils.toString(result));
        } catch (Throwable e) {
            long elapsedMs = DateUtils.currentEpochMillis() - start;
            log.atError()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.METHOD, methodName)
                    .addKeyValue(LoggerKey.ARGUMENTS, arguments)
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .setCause(e)
                    .log(e.toString());
            throw e;
        }

        return result;
    }
}
