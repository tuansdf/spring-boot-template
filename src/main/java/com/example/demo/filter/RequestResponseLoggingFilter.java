package com.example.demo.filter;

import com.example.demo.config.RequestContextHolder;
import com.example.demo.constant.HTTPHeader;
import com.example.demo.util.ConversionUtils;
import com.example.demo.util.RandomUtils;
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
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        try {
            RequestContextHolder.get().setRequestId(ConversionUtils.safeToString(RandomUtils.Insecure.generateTimeBasedUUID()));
            RequestContextHolder.get().setLocale(httpServletRequest.getLocale());
            RequestContextHolder.get().setTenantId(httpServletRequest.getHeader(HTTPHeader.X_TENANT_ID));
            RequestContextHolder.syncMDC();

            log.info("ENTER method={} path={} query={}", httpServletRequest.getMethod(), httpServletRequest.getServletPath(), httpServletRequest.getQueryString());
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            log.info("EXIT  status={}", httpServletResponse.getStatus());
            RequestContextHolder.clear();
        }
    }

}
