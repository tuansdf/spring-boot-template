package com.example.sbt.features.auth.controller;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.infrastructure.exception.ExceptionHelper;
import com.example.sbt.infrastructure.web.helper.LocaleHelper;
import com.example.sbt.features.auth.dto.*;
import com.example.sbt.features.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public/v1/auth")
public class PublicAuthController {
    private final LocaleHelper localeHelper;
    private final ExceptionHelper exceptionHelper;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest requestDTO) {
        RequestContextHolder.set(RequestContextHolder.get().withUsername(requestDTO.getUsername()));
        var result = authService.login(requestDTO, RequestContextHolder.get());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> register(@RequestBody @Valid RegisterRequest requestDTO) {
        authService.register(requestDTO, RequestContextHolder.get());
        var message = localeHelper.getMessage("auth.activate_account_email_sent");
        return ResponseEntity.ok(new CommonResponse<>(message, HttpStatus.OK));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<CommonResponse<Object>> requestResetPassword(@RequestBody @Valid RequestResetPasswordRequest requestDTO) {
        authService.requestResetPassword(requestDTO);
        var message = localeHelper.getMessage("auth.reset_password_email_sent");
        return ResponseEntity.ok(new CommonResponse<>(message, HttpStatus.OK));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<CommonResponse<Object>> resetPassword(@RequestBody @Valid ResetPasswordRequest requestDTO) {
        authService.resetPassword(requestDTO);
        var message = localeHelper.getMessage("auth.reset_password_success");
        return ResponseEntity.ok(new CommonResponse<>(message, HttpStatus.OK));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<LoginResponse>> refreshAccessToken(@RequestBody RefreshTokenRequest requestDTO) {
        var result = authService.refreshAccessToken(requestDTO.getToken(), RequestContextHolder.get());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping("/account/activate")
    public ModelAndView activateAccount(@RequestParam(required = false) String token) {
        String result = localeHelper.getMessage("common.error");
        try {
            if (StringUtils.isNotEmpty(token)) {
                authService.activateAccount(token);
                result = localeHelper.getMessage("auth.activate_account_success");
            }
        } catch (Exception e) {
            result = exceptionHelper.toResponse(e).getMessage();
        }
        ModelAndView mav = new ModelAndView("centered");
        mav.addObject("title", localeHelper.getMessage("email.activate_account_subject"));
        mav.addObject("message", result);
        return mav;
    }

    @PostMapping("/account/activate/request")
    public ResponseEntity<CommonResponse<Object>> requestActivateAccount(@RequestBody @Valid RequestActivateAccountRequest requestDTO) {
        authService.requestActivateAccount(requestDTO);
        var message = localeHelper.getMessage("auth.activate_account_email_sent");
        return ResponseEntity.ok(new CommonResponse<>(message, HttpStatus.OK));
    }
}
