package com.example.sbt.common.filter;

import com.example.sbt.common.constant.CommonType;
import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.module.jwt.JWTService;
import com.example.sbt.module.jwt.dto.JWTPayload;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_START_WITH = "Bearer ";
    private static final int TOKEN_START_AT = AUTHORIZATION_START_WITH.length();

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header) || !header.startsWith(AUTHORIZATION_START_WITH)) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        final String jwt = header.substring(TOKEN_START_AT);
        JWTPayload jwtPayload = jwtService.verify(jwt);
        if (jwtPayload == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (!CommonType.ACCESS_TOKEN.equals(CommonType.fromIndex(jwtPayload.getType()))) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Set<String> permissions = PermissionCode.fromIndexes(jwtPayload.getPermissions());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtPayload.getSubject(),
                null,
                permissions.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestHolder.getContext().setUserId(ConversionUtils.toUUID(jwtPayload.getSubject()));
        RequestHolder.getContext().setPermissions(permissions);
        RequestHolder.syncMDC();

        chain.doFilter(servletRequest, servletResponse);
    }

}