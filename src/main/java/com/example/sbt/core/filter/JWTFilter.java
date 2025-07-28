package com.example.sbt.core.filter;

import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.helper.RequestHelper;
import com.example.sbt.module.authtoken.service.JWTService;
import com.example.sbt.shared.util.ConversionUtils;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {
    private final RequestHelper requestHelper;
    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = requestHelper.getBearerToken(request);
        JWTPayload jwtPayload = jwtService.verify(jwt);
        if (jwtPayload == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!CommonType.ACCESS_TOKEN.equals(CommonType.fromIndex(jwtPayload.getType()))) {
            filterChain.doFilter(request, response);
            return;
        }

        List<String> permissions = PermissionCode.fromIndexes(jwtPayload.getPermissions());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                jwtPayload.getSubject(),
                null,
                permissions.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RequestContext.get().setUserId(ConversionUtils.toUUID(jwtPayload.getSubject()));
        RequestContext.get().setUsername(jwtPayload.getSubject());
        RequestContext.syncWithLogger();

        filterChain.doFilter(request, response);
    }
}