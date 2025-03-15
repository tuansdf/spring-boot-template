package com.example.demo.module.auth;

import com.example.demo.common.constant.CommonType;
import com.example.demo.common.constant.PermissionCode;
import com.example.demo.common.dto.CommonResponse;
import com.example.demo.common.dto.RequestContextHolder;
import com.example.demo.common.util.ExceptionUtils;
import com.example.demo.module.auth.dto.AuthDTO;
import com.example.demo.module.token.TokenService;
import com.example.demo.module.user.dto.ChangePasswordRequestDTO;
import com.example.demo.module.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;
    private final AuthService authService;

    @PatchMapping("/password")
    @Secured({PermissionCode.SYSTEM_ADMIN})
    public ResponseEntity<CommonResponse<UserDTO>> changePassword(@RequestBody ChangePasswordRequestDTO requestDTO) {
        try {
            UUID userId = RequestContextHolder.get().getUserId();
            var result = authService.changePassword(requestDTO, userId);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }


    @PostMapping("/token/revoke")
    public ResponseEntity<CommonResponse<Object>> revokeRefreshTokens() {
        try {
            UUID userId = RequestContextHolder.get().getUserId();
            tokenService.deactivatePastTokens(userId, CommonType.REFRESH_TOKEN);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<CommonResponse<Object>> enableOtp() {
        try {
            UUID userId = RequestContextHolder.get().getUserId();
            var result = authService.enableOtp(userId);
            return ResponseEntity.ok(new CommonResponse<>(result));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/2fa/confirm")
    public ResponseEntity<CommonResponse<Object>> confirmOtp(@RequestBody AuthDTO requestDTO) {
        try {
            UUID userId = RequestContextHolder.get().getUserId();
            authService.confirmOtp(requestDTO, userId);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<CommonResponse<Object>> disableOtp(@RequestBody AuthDTO requestDTO) {
        try {
            UUID userId = RequestContextHolder.get().getUserId();
            authService.disableOtp(requestDTO, userId);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
