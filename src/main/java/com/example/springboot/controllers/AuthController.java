package com.example.springboot.controllers;

import com.example.springboot.constants.CommonType;
import com.example.springboot.dtos.CommonResponse;
import com.example.springboot.modules.token.TokenService;
import com.example.springboot.utils.AuthUtils;
import com.example.springboot.utils.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/token/revoke")
    public ResponseEntity<CommonResponse<Object>> revokeRefreshTokens() {
        try {
            var principal = AuthUtils.getAuthenticationPrincipal();
            tokenService.deactivatePastTokens(principal.getUserId(), CommonType.REFRESH_TOKEN);
            return ResponseEntity.ok(new CommonResponse<>());
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
