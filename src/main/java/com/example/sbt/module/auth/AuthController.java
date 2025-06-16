package com.example.sbt.module.auth;

import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.RequestHolder;
import com.example.sbt.module.auth.dto.ConfirmOtpRequestDTO;
import com.example.sbt.module.auth.dto.DisableOtpRequestDTO;
import com.example.sbt.module.auth.dto.EnableOtpRequestDTO;
import com.example.sbt.module.token.TokenService;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthService authService;

    @PatchMapping("/password")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<Object>> changePassword(@RequestBody ChangePasswordRequestDTO requestDTO) {
        UUID userId = RequestHolder.getContext().getUserId();
        authService.changePassword(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @GetMapping("/token/verify")
    public ResponseEntity<CommonResponse<Object>> verifyToken() {
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @PostMapping("/token/revoke")
    public ResponseEntity<CommonResponse<Object>> revokeAllTokens() {
        UUID userId = RequestHolder.getContext().getUserId();
        tokenService.deactivatePastTokensByUserId(userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<CommonResponse<Object>> enableOtp(@RequestBody EnableOtpRequestDTO requestDTO) {
        UUID userId = RequestHolder.getContext().getUserId();
        var result = authService.enableOtp(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>(result));
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<CommonResponse<Object>> confirmOtp(@RequestBody ConfirmOtpRequestDTO requestDTO) {
        UUID userId = RequestHolder.getContext().getUserId();
        authService.confirmOtp(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<CommonResponse<Object>> disableOtp(@RequestBody DisableOtpRequestDTO requestDTO) {
        UUID userId = RequestHolder.getContext().getUserId();
        authService.disableOtp(requestDTO, userId);
        return ResponseEntity.ok(new CommonResponse<>());
    }

}
