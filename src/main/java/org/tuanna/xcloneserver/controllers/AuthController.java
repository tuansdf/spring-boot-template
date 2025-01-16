package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.constants.CommonType;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.utils.AuthUtils;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

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
