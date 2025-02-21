package com.example.demo.controllers;

import com.example.demo.dtos.CommonResponse;
import com.example.demo.modules.auth.AuthService;
import com.example.demo.modules.auth.dtos.*;
import com.example.demo.utils.ExceptionUtils;
import com.example.demo.utils.I18nHelper;
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

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthDTO>> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            var result = authService.login(requestDTO);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> register(@RequestBody RegisterRequestDTO requestDTO) {
        try {
            authService.register(requestDTO);
            var message = I18nHelper.getMessage("auth.activate_account_email_sent");
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<CommonResponse<Object>> forgotPassword(@RequestBody ForgotPasswordRequestDTO requestDTO) {
        try {
            authService.forgotPassword(requestDTO);
            var message = I18nHelper.getMessage("auth.reset_password_email_sent");
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CommonResponse<Object>> resetPassword(@RequestBody ResetPasswordRequestDTO requestDTO) {
        try {
            authService.resetPassword(requestDTO);
            var message = I18nHelper.getMessage("auth.reset_password_success");
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
    public ResponseEntity<CommonResponse<Object>> activateAccount(@RequestBody AuthDTO requestDTO) {
        try {
            authService.activateAccount(requestDTO.getToken());
            var message = I18nHelper.getMessage("auth.activate_account_success");
            return ResponseEntity.ok(new CommonResponse<>(message));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
