package com.example.springboot.controllers;

import com.example.springboot.dtos.CommonResponse;
import com.example.springboot.modules.authentication.AuthService;
import com.example.springboot.modules.authentication.dtos.*;
import com.example.springboot.utils.ExceptionUtils;
import com.example.springboot.utils.I18nUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public/auth")
public class PublicAuthController {

    private final AuthService authService;
    private final I18nUtils i18nUtils;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthDTO>> login(
            HttpServletResponse servletResponse, @RequestBody LoginRequestDTO requestDTO) {
        try {
            var result = authService.login(requestDTO, servletResponse.getLocale());
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> register(
            HttpServletResponse servletResponse, @RequestBody RegisterRequestDTO requestDTO) {
        try {
            authService.register(requestDTO, servletResponse.getLocale());
            var message = i18nUtils.getMessage("auth.activate_account_email_sent", servletResponse.getLocale());
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CommonResponse<Object>> forgotPassword(
            HttpServletRequest servletRequest, @RequestBody ForgotPasswordRequestDTO requestDTO) {
        try {
            authService.forgotPassword(requestDTO, servletRequest.getLocale());
            var message = i18nUtils.getMessage("auth.reset_password_email_sent", servletRequest.getLocale());
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CommonResponse<Object>> resetPassword(
            HttpServletRequest servletRequest, @RequestBody ResetPasswordRequestDTO requestDTO) {
        try {
            authService.resetPassword(requestDTO, servletRequest.getLocale());
            var message = i18nUtils.getMessage("auth.reset_password_success", servletRequest.getLocale());
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<AuthDTO>> refreshAccessToken(@RequestBody AuthDTO requestDTO) {
        try {
            var result = authService.refreshAccessToken(requestDTO.getToken());
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/account/activate")
    public ResponseEntity<CommonResponse<Object>> activateAccount(
            HttpServletRequest servletRequest, @RequestBody AuthDTO requestDTO) {
        try {
            authService.activateAccount(requestDTO.getToken());
            var message = i18nUtils.getMessage("auth.activate_account_success", servletRequest.getLocale());
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
