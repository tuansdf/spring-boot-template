package org.tuanna.xcloneserver.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.dtos.CommonResponse;
import org.tuanna.xcloneserver.modules.authentication.AuthService;
import org.tuanna.xcloneserver.modules.authentication.dtos.AuthResponseDTO;
import org.tuanna.xcloneserver.modules.authentication.dtos.LoginRequestDTO;
import org.tuanna.xcloneserver.modules.authentication.dtos.RegisterRequestDTO;
import org.tuanna.xcloneserver.utils.ExceptionUtils;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/public/auth")
public class PublicAuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<AuthResponseDTO>> login(@RequestBody LoginRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(authService.login(requestDTO)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<AuthResponseDTO>> register(@RequestBody RegisterRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(authService.register(requestDTO)));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<CommonResponse<AuthResponseDTO>> refreshAccessToken(@RequestBody AuthResponseDTO requestDTO) {
        try {
            return ResponseEntity.ok(new CommonResponse<>(authService.refreshAccessToken(requestDTO.getRefreshToken())));
        } catch (Exception e) {
            return ExceptionUtils.toResponseEntity(e);
        }
    }

}
