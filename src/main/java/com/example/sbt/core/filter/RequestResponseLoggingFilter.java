package com.example.sbt.core.filter;

import com.example.sbt.core.constant.HTTPHeader;
import com.example.sbt.core.constant.LoggerKey;
import com.example.sbt.core.dto.RequestContextHolder;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.DateUtils;
import com.example.sbt.shared.util.RandomUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        long start = DateUtils.currentEpochMillis();

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            RequestContextHolder.get().setRequestId(ConversionUtils.safeToString(RandomUtils.insecure().randomHexString(8)));
            RequestContextHolder.get().setLocale(httpRequest.getLocale());
            RequestContextHolder.get().setTenantId(httpRequest.getHeader(HTTPHeader.X_TENANT_ID));
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
            long elapsedMs = DateUtils.currentEpochMillis() - start;
            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.USER_ID, RequestContextHolder.get().getUserId())
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
