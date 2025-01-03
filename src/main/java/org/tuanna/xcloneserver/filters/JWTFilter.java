package org.tuanna.xcloneserver.filters;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tuanna.xcloneserver.constants.Constants;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.modules.auth.AuthUtils;
import org.tuanna.xcloneserver.utils.CommonUtils;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain chain) throws ServletException, IOException {
        try {
            final String header = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (Strings.isNullOrEmpty(header) || !header.startsWith("Bearer ")) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }

            final String jwt = header.split(" ")[1].trim();
            JWTPayload jwtPayload = jwtService.verify(jwt);
            if (jwtPayload == null) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }

            boolean isValid = true;
            if (!Strings.isNullOrEmpty(jwtPayload.getTokenId())) {
                isValid = tokenService.validateTokenById(CommonUtils.safeToUUID(jwtPayload.getTokenId()), jwt, TokenType.REFRESH_TOKEN);
            } else {
                isValid = TokenType.ACCESS_TOKEN.equals(jwtPayload.getType());
            }
            if (!isValid) {
                chain.doFilter(servletRequest, servletResponse);
                return;
            }

            AuthUtils.setAuthentication(jwtPayload);

            MDC.put(Constants.KEY_IN_MDC.USER_ID, jwtPayload.getSubjectId());

            chain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(Constants.KEY_IN_MDC.USER_ID);
        }
    }

}
