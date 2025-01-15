package org.tuanna.xcloneserver.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tuanna.xcloneserver.constants.MDCKey;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.utils.AuthUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_START_WITH = "Bearer ";
    private static final int TOKEN_START_AT = AUTHORIZATION_START_WITH.length();

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        try {
            final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isEmpty(header) || !header.startsWith(AUTHORIZATION_START_WITH)) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }

            final String jwt = header.substring(TOKEN_START_AT);
            JWTPayload jwtPayload = jwtService.verify(jwt);
            if (jwtPayload == null) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }

            if (!TokenType.toIndex(TokenType.ACCESS_TOKEN).equals(jwtPayload.getType())) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }

            AuthUtils.setAuthentication(jwtPayload);

            MDC.put(MDCKey.USER_ID, jwtPayload.getSubjectId());

            chain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(MDCKey.USER_ID);
        }
    }

}