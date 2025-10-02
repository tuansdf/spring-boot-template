package com.example.sbt.infrastructure.security;

import com.example.sbt.common.constant.CustomProperties;
import com.example.sbt.common.util.ConversionUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2FailureHandler implements AuthenticationFailureHandler {
    private final CustomProperties customProperties;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("oauth2", exception);
        response.sendRedirect(ConversionUtils.safeToString(customProperties.getOauth2CallbackUrl())
                .replace("{code}", "").replace("{error}", ""));
    }
}