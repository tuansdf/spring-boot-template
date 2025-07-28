package com.example.sbt.core.helper;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class RequestHelper {
    private static final String AUTHORIZATION_START_WITH = "Bearer ";
    private static final int TOKEN_START_AT = AUTHORIZATION_START_WITH.length();

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
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header) || !header.startsWith(AUTHORIZATION_START_WITH)) {
            return null;
        }

        return header.substring(TOKEN_START_AT);
    }
}
