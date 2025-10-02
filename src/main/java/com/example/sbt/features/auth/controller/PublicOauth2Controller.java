package com.example.sbt.features.auth.controller;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.features.auth.dto.LoginResponse;
import com.example.sbt.features.auth.dto.RefreshTokenRequest;
import com.example.sbt.features.auth.service.AuthService;
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
@RequestMapping("/public/oauth2")
public class PublicOauth2Controller {
    private final AuthService authService;

    @PostMapping("/token/exchange")
    public ResponseEntity<CommonResponse<LoginResponse>> exchangeToken(@RequestBody RefreshTokenRequest requestDTO) {
        var result = authService.exchangeOauth2Token(requestDTO.getToken(), RequestContextHolder.get());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }
}
