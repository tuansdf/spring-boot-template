package com.example.sbt.infrastructure.security;

import com.example.sbt.infrastructure.web.config.CustomProperties;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.exception.ExceptionHelper;
import com.example.sbt.infrastructure.web.helper.LocaleHelper;
import com.example.sbt.features.authtoken.dto.AuthTokenDTO;
import com.example.sbt.features.authtoken.service.AuthTokenService;
import com.example.sbt.features.configuration.service.Configurations;
import com.example.sbt.features.user.entity.User;
import com.example.sbt.features.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final CustomProperties customProperties;
    private final Configurations configurations;
    private final LocaleHelper localeHelper;
    private final AuthTokenService authTokenService;
    private final UserRepository userRepository;
    private final ExceptionHelper exceptionHelper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            if (authentication.getPrincipal() instanceof DefaultOAuth2User oauth2User) {
                String email = ConversionUtils.safeToString(oauth2User.getAttribute("email")).trim().toLowerCase();
                if (StringUtils.isBlank(email)) {
                    throw new RuntimeException("Email is blank");
                }
                User user = userRepository.findTopByEmail(email).orElse(null);
                if (user == null) {
                    Boolean isRegistrationEnabled = configurations.isRegistrationEnabled();
                    if (isRegistrationEnabled != null && !isRegistrationEnabled) {
                        throw new CustomException(localeHelper.getMessage("auth.error.registration_disabled"), HttpStatus.BAD_REQUEST);
                    }
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setEmail(email);
                    newUser.setPassword(null);
                    newUser.setName(oauth2User.getName());
                    newUser.setIsEnabled(true);
                    newUser.setIsVerified(true);
                    user = userRepository.save(newUser);
                }
                if (!ConversionUtils.safeToBoolean(user.getIsEnabled())) {
                    throw new CustomException(localeHelper.getMessage("auth.error.account_disabled"), HttpStatus.UNAUTHORIZED);
                }
                AuthTokenDTO tokenDTO = authTokenService.createOauth2ExchangeToken(user.getId());
                response.sendRedirect(ConversionUtils.safeToString(customProperties.getOauth2CallbackUrl())
                        .replace("{code}", tokenDTO.getValue()).replace("{error}", ""));
            }
        } catch (Exception e) {
            if (e instanceof CustomException) {
                log.error("onAuthenticationSuccess {}", e.toString());
            } else {
                log.error("onAuthenticationSuccess", e);
            }
            response.sendRedirect(ConversionUtils.safeToString(customProperties.getOauth2CallbackUrl())
                    .replace("{code}", "").replace("{error}", exceptionHelper.toResponseMessage(e)));
        }
    }
}