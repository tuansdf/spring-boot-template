package com.example.sbt.core.filter;

import com.example.sbt.core.constant.HTTPHeader;
import com.example.sbt.core.constant.LoggerKey;
import com.example.sbt.core.dto.RequestContext;
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        long start = DateUtils.currentEpochMillis();

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        try {
            RequestContext.get().setRequestId(ConversionUtils.safeToString(RandomUtils.insecure().randomHexString(8)));
            RequestContext.get().setLocale(httpServletRequest.getLocale());
            RequestContext.get().setTenantId(httpServletRequest.getHeader(HTTPHeader.X_TENANT_ID));
            RequestContext.syncWithLogger();

            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "ENTER")
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.HTTP_METHOD, httpServletRequest.getMethod())
                    .addKeyValue(LoggerKey.HTTP_PATH, httpServletRequest.getServletPath())
                    .addKeyValue(LoggerKey.HTTP_QUERY, httpServletRequest.getQueryString())
                    .log();
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            long elapsedMs = DateUtils.currentEpochMillis() - start;
            log.atInfo()
                    .addKeyValue(LoggerKey.EVENT, "EXIT")
                    .addKeyValue(LoggerKey.USER_ID, RequestContext.get().getUserId())
                    .addKeyValue(LoggerKey.AROUND_KEY, start)
                    .addKeyValue(LoggerKey.HTTP_METHOD, httpServletRequest.getMethod())
                    .addKeyValue(LoggerKey.HTTP_PATH, httpServletRequest.getServletPath())
                    .addKeyValue(LoggerKey.HTTP_QUERY, httpServletRequest.getQueryString())
                    .addKeyValue(LoggerKey.HTTP_STATUS, httpServletResponse.getStatus())
                    .addKeyValue(LoggerKey.ELAPSED_MS, elapsedMs)
                    .log();
            RequestContext.clear();
        }
    }
}
