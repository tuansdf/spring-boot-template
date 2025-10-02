package com.example.sbt.infrastructure.filter;

import com.example.sbt.common.dto.JWTPayload;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.infrastructure.helper.ServletHelper;
import com.example.sbt.features.authtoken.entity.AuthToken;
import com.example.sbt.features.authtoken.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {
    private final ServletHelper servletHelper;
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = servletHelper.getBearerToken(request);
        JWTPayload jwtPayload = jwtService.verify(jwt);
        if (jwtPayload == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!AuthToken.Type.ACCESS_TOKEN.equals(jwtPayload.getType())) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> permissions = jwtPayload.getPermissions();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtPayload.getSubject(),
                null,
                permissions != null ? permissions.stream().map(SimpleGrantedAuthority::new).toList() : null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UUID userId = ConversionUtils.toUUID(jwtPayload.getSubject());
        String username = null;
        if (userId == null) username = jwtPayload.getSubject();
        RequestContextHolder.set(RequestContextHolder.get()
                .withUserId(userId)
                .withUsername(username));

        filterChain.doFilter(request, response);
    }
}