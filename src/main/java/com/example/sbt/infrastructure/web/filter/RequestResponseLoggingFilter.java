package com.example.sbt.infrastructure.web.filter;

import com.example.sbt.common.constant.HTTPHeader;
import com.example.sbt.common.constant.LoggerKey;
import com.example.sbt.common.dto.RequestContext;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.RandomUtils;
import com.example.sbt.infrastructure.web.helper.ServletHelper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

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
            String timeZone = httpRequest.getHeader(HTTPHeader.X_TIME_ZONE);
            ZoneOffset zoneOffset = null;
            if (StringUtils.isNotBlank(timeZone)) {
                zoneOffset = ZoneId.of(timeZone).getRules().getOffset(Instant.now());
            }
            RequestContext requestContext = RequestContext.builder()
                    .requestId(ConversionUtils.toString(RandomUtils.insecure().randomUUID()))
                    .locale(LocaleContextHolder.getLocale())
                    .tenantId(httpRequest.getHeader(HTTPHeader.X_TENANT_ID))
                    .ip(servletHelper.getClientIp(httpRequest))
                    .zoneOffset(zoneOffset)
                    .build();
            RequestContextHolder.set(requestContext);

            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "ENTER")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.HTTP_METHOD, httpRequest.getMethod())
                    .addKeyValue(LoggerKey.HTTP_PATH, httpRequest.getServletPath())
                    .addKeyValue(LoggerKey.HTTP_QUERY, httpRequest.getQueryString())
                    .addKeyValue(LoggerKey.CONTEXT, requestContext)
                    .log();
            filterChain.doFilter(request, response);
        } finally {
            double elapsedMs = (System.nanoTime() - start) / 1_000_000.0;
            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.HTTP_METHOD, httpRequest.getMethod())
                    .addKeyValue(LoggerKey.HTTP_PATH, httpRequest.getServletPath())
                    .addKeyValue(LoggerKey.HTTP_STATUS, httpResponse.getStatus())
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .log();
            RequestContextHolder.clear();
        }
    }
}
