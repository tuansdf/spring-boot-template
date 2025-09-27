package com.example.sbt.infrastructure.config;

import com.example.sbt.common.constant.CustomProperties;
import com.example.sbt.common.dto.JWTPayload;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.helper.LocaleHelper;
import com.example.sbt.module.authtoken.service.JWTService;
import com.example.sbt.module.user.entity.User;
import com.example.sbt.module.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {
    private final CustomProperties customProperties;
    private final LocaleHelper localeHelper;
    private final JWTService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            User user = userRepository.findTopByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(email);
                newUser.setEmail(email);
                newUser.setPassword(null);
                newUser.setName(oAuth2User.getName());
                newUser.setIsEnabled(true);
                newUser.setIsVerified(true);
                return userRepository.save(newUser);
            });
            Boolean isRegistrationEnabled = configurations.isRegistrationEnabled();
            if (isRegistrationEnabled != null && !isRegistrationEnabled) {
                throw new CustomException(localeHelper.getMessage("auth.error.registration_disabled"), HttpStatus.BAD_REQUEST);
            }
            if (!ConversionUtils.safeToBoolean(user.getIsEnabled())) {
                throw new CustomException(localeHelper.getMessage("auth.error.account_disabled"), HttpStatus.UNAUTHORIZED);
            }
            JWTPayload jwtPayload = jwtService.createOauth2Jwt(user.getId());
            response.sendRedirect(ConversionUtils.safeToString(customProperties.getOauth2CallbackUrl()).replace("{code}", jwtPayload.getValue()));
        }
    }
}