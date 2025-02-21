package com.example.demo.filters;

import com.example.demo.configs.RequestContextHolder;
import com.example.demo.constants.CommonType;
import com.example.demo.constants.PermissionCode;
import com.example.demo.modules.jwt.JWTService;
import com.example.demo.modules.jwt.dtos.JWTPayload;
import com.example.demo.utils.ConversionUtils;
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

        if (!CommonType.toIndex(CommonType.ACCESS_TOKEN).equals(jwtPayload.getType())) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Set<String> permissions = PermissionCode.fromIndexes(jwtPayload.getPermissions());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtPayload.getSubject(),
                null,
                permissions.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestContextHolder.get().setUserId(ConversionUtils.toUUID(jwtPayload.getSubject()));
        RequestContextHolder.get().setPermissions(permissions);
        RequestContextHolder.syncMDC();

        chain.doFilter(servletRequest, servletResponse);
    }

}