package com.example.sbt.features.auth.controller;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.features.auth.dto.ConfirmOtpRequest;
import com.example.sbt.features.auth.dto.DisableOtpRequest;
import com.example.sbt.features.auth.dto.EnableOtpRequest;
import com.example.sbt.features.auth.service.AuthService;
import com.example.sbt.features.authtoken.service.AuthTokenService;
import com.example.sbt.features.user.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private final AuthTokenService authTokenService;
    private final AuthService authService;

    @PatchMapping("/password")
    public ResponseEntity<CommonResponse<Object>> changePassword(@RequestBody ChangePasswordRequest requestDTO) {
        UUID userId = RequestContextHolder.get().getUserId();
        authService.changePassword(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @GetMapping("/token/verify")
    public ResponseEntity<CommonResponse<Object>> verifyToken() {
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @PostMapping("/token/revoke")
    public ResponseEntity<CommonResponse<Object>> revokeAllTokens() {
        UUID userId = RequestContextHolder.get().getUserId();
        authTokenService.invalidateByUserId(userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<CommonResponse<Object>> enableOtp(@RequestBody EnableOtpRequest requestDTO) {
        UUID userId = RequestContextHolder.get().getUserId();
        var result = authService.enableOtp(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<CommonResponse<Object>> confirmOtp(@RequestBody ConfirmOtpRequest requestDTO) {
        UUID userId = RequestContextHolder.get().getUserId();
        authService.confirmOtp(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<CommonResponse<Object>> disableOtp(@RequestBody DisableOtpRequest requestDTO) {
        UUID userId = RequestContextHolder.get().getUserId();
        authService.disableOtp(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }
}
