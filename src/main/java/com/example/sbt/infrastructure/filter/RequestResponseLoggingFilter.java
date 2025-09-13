package com.example.sbt.infrastructure.filter;

import com.example.sbt.common.constant.HTTPHeader;
import com.example.sbt.common.constant.LoggerKey;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.RandomUtils;
import com.example.sbt.infrastructure.helper.ServletHelper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {
    private final ServletHelper servletHelper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        long start = System.nanoTime();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            RequestContextHolder.get().setRequestId(ConversionUtils.safeToString(RandomUtils.insecure().randomUUID()));
            RequestContextHolder.get().setLocale(LocaleContextHolder.getLocale());
            RequestContextHolder.get().setTenantId(httpRequest.getHeader(HTTPHeader.X_TENANT_ID));
            RequestContextHolder.get().setIp(servletHelper.getClientIp(httpRequest));
            RequestContextHolder.syncWithLogger();

            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "ENTER")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.HTTP_METHOD, httpRequest.getMethod())
                    .addKeyValue(LoggerKey.HTTP_PATH, httpRequest.getServletPath())
                    .addKeyValue(LoggerKey.HTTP_QUERY, httpRequest.getQueryString())
                    .log();
            filterChain.doFilter(request, response);
        } finally {
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.HTTP_METHOD, httpRequest.getMethod())
                    .addKeyValue(LoggerKey.HTTP_PATH, httpRequest.getServletPath())
                    .addKeyValue(LoggerKey.HTTP_QUERY, httpRequest.getQueryString())
                    .addKeyValue(LoggerKey.HTTP_STATUS, httpResponse.getStatus())
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .log();
            RequestContextHolder.clear();
        }
    }
}
