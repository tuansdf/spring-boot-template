package com.example.sbt.module.auth.controller;

import com.example.sbt.core.dto.CommonResponse;
import com.example.sbt.core.helper.ExceptionHelper;
import com.example.sbt.core.helper.HTMLTemplate;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/open/v1/auth")
public class PublicAuthController {

    private final LocaleHelper localeHelper;
    private final ExceptionHelper exceptionHelper;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthDTO>> login(@RequestBody LoginRequestDTO requestDTO) {
        var result = authService.login(requestDTO);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> register(@RequestBody RegisterRequestDTO requestDTO) {
        authService.register(requestDTO);
        var message = localeHelper.getMessage("auth.activate_account_email_sent");
        return ResponseEntity.ok(new CommonResponse<>(message));
    }

    @PostMapping("/password/reset/request")
    public ResponseEntity<CommonResponse<Object>> requestResetPassword(@RequestBody RequestResetPasswordRequestDTO requestDTO) {
        authService.requestResetPassword(requestDTO);
        var message = localeHelper.getMessage("auth.reset_password_email_sent");
        return ResponseEntity.ok(new CommonResponse<>(message));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<CommonResponse<Object>> resetPassword(@RequestBody ResetPasswordRequestDTO requestDTO) {
        authService.resetPassword(requestDTO);
        var message = localeHelper.getMessage("auth.reset_password_success");
        return ResponseEntity.ok(new CommonResponse<>(message));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<RefreshTokenResponseDTO>> refreshAccessToken(@RequestBody AuthDTO requestDTO) {
        var result = authService.refreshAccessToken(requestDTO.getToken());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @GetMapping(value = "/account/activate", produces = MediaType.TEXT_HTML_VALUE)
    public String activateAccount(@RequestParam(required = false) String token) {
        String result = localeHelper.getMessage("common.error");
        try {
            if (StringUtils.isNotEmpty(token)) {
                authService.activateAccount(token);
                result = localeHelper.getMessage("auth.activate_account_success");
            }
        } catch (Exception e) {
            result = exceptionHelper.toResponse(e).getMessage();
        }
        return HTMLTemplate.createCenteredHtml(localeHelper.getMessage("email.activate_account_subject"), result);
    }

    @PostMapping("/account/activate/request")
    public ResponseEntity<CommonResponse<Object>> requestActivateAccount(@RequestBody RequestActivateAccountRequestDTO requestDTO) {
        authService.requestActivateAccount(requestDTO);
        var message = localeHelper.getMessage("auth.activate_account_email_sent");
        return ResponseEntity.ok(new CommonResponse<>(message));
    }

}
