package com.example.sbt.core.filter;

import com.example.sbt.core.constant.HTTPHeader;
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
            RequestContext.get().setRequestId(ConversionUtils.safeToString(RandomUtils.Insecure.generateHexString(8)));
            RequestContext.get().setLocale(httpServletRequest.getLocale());
            RequestContext.get().setTenantId(httpServletRequest.getHeader(HTTPHeader.X_TENANT_ID));
            RequestContext.syncMDC();

            log.info("[requestAt={}] ENTER [method={}] [path={}] [query={}]", start, httpServletRequest.getMethod(), httpServletRequest.getServletPath(), httpServletRequest.getQueryString());
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            long execTime = DateUtils.currentEpochMillis() - start;
            log.info("[requestAt={}] EXIT  [userId={}] [method={}] [path={}] [execTime={} ms] [status={}]", start, RequestContext.get().getUserId(), httpServletRequest.getMethod(), httpServletRequest.getServletPath(), execTime, httpServletResponse.getStatus());
            RequestContext.clear();
        }
    }

}
