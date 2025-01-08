package org.tuanna.xcloneserver.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tuanna.xcloneserver.constants.Constants;
import org.tuanna.xcloneserver.utils.UUIDUtils;

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
            MDC.put(Constants.KEY_IN_MDC.REQUEST_ID, UUIDUtils.generateId().toString());

            log.info("ENTER method={} path={} query={}", httpServletRequest.getMethod(), httpServletRequest.getServletPath(), httpServletRequest.getQueryString());
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            log.info("EXIT status={}", httpServletResponse.getStatus());
            MDC.clear();
        }
    }

}
