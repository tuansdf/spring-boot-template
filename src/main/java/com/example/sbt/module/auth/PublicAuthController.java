package com.example.sbt.module.auth;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.util.ExceptionUtils;
import com.example.sbt.common.util.HTMLService;
import com.example.sbt.common.util.I18nHelper;
import com.example.sbt.module.auth.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            authService.refreshAccessToken(requestDTO.getToken());
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @GetMapping(value = "/account/activate", produces = MediaType.TEXT_HTML_VALUE)
    public String activateAccount(@RequestParam(required = false) String token) {
        String result = I18nHelper.getMessage("common.error");
        try {
            if (StringUtils.isNotEmpty(token)) {
                authService.activateAccount(token);
                result = I18nHelper.getMessage("auth.activate_account_success");
            }
        } catch (Exception e) {
            result = ExceptionUtils.toResponse(e).getMessage();
        }
        return HTMLService.createCenteredHTML(I18nHelper.getMessage("email.activate_account_subject"), result);
    }

}
