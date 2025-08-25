package com.example.sbt.core.filter;

import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ServletHelper;
import com.example.sbt.module.configuration.service.Configurations;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(2)
public class IPWhitelistFilter extends OncePerRequestFilter {
    private final LocaleHelper localeHelper;
    private final Configurations configurations;
    private final ServletHelper servletHelper;

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isIpWhitelisted(servletHelper.getClientIp(request))) {
            CommonResponse<Object> commonResponse = new CommonResponse<>();
            commonResponse.setMessage(localeHelper.getMessage("auth.error.invalid_ip"));
            commonResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            servletHelper.sendJson(response, commonResponse, HttpStatus.UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private boolean isIpWhitelisted(String ip) {
        if (StringUtils.isBlank(ip)) return false;
        String ipWhitelist = configurations.getIpWhitelist();
        return ipWhitelist == null || ipWhitelist.contains(ip);
    }
}
