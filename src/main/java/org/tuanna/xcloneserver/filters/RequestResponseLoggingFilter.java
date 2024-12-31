package org.tuanna.xcloneserver.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.tuanna.xcloneserver.constants.Constants;
import org.tuanna.xcloneserver.utils.DateUtils;

import java.io.IOException;

@Slf4j
@Component
@Order(1)
public class RequestResponseLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            MDC.put(Constants.KEY_IN_MDC.REQUEST_ID, String.valueOf(DateUtils.getEpochMicro()));

            HttpServletRequest request = (HttpServletRequest) servletRequest;
            log.info("ENTER method={} path={} query={}", request.getMethod(), request.getServletPath(), request.getQueryString());
            filterChain.doFilter(servletRequest, servletResponse);
            log.info("EXIT");
        } finally {
            MDC.clear();
        }
    }

}
