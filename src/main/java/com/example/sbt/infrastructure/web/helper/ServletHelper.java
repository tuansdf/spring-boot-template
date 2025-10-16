package com.example.sbt.infrastructure.web.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class ServletHelper {
    private static final String AUTHORIZATION_START_WITH = "Bearer ";
    private static final int TOKEN_START_AT = AUTHORIZATION_START_WITH.length();

    private final ObjectMapper objectMapper;

    private boolean isValidIp(String ip) {
        return StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    public String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (!isValidIp(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (!isValidIp(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!isValidIp(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        if (!isValidIp(ipAddress)) {
            return null;
        }
        if (ipAddress.contains(",")) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }

    public String getBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(bearerToken)) {
            if (!bearerToken.startsWith(AUTHORIZATION_START_WITH)) {
                return null;
            }
            return bearerToken.substring(TOKEN_START_AT);
        }

        Cookie[] cookies = request.getCookies();
        if (ObjectUtils.isEmpty(cookies)) return null;
        for (Cookie cookie : cookies) {
            if (HttpHeaders.AUTHORIZATION.equalsIgnoreCase(cookie.getName())) {
                return StringUtils.trimToNull(cookie.getValue());
            }
        }

        return null;
    }

    public void sendJson(HttpServletResponse response, Object json, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        if (json != null) {
            response.getWriter().write(objectMapper.writeValueAsString(json));
        }
        response.getWriter().flush();
    }
}
