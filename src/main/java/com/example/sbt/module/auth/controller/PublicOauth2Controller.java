package com.example.sbt.module.auth.controller;

import com.example.sbt.common.dto.CommonResponse;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.infrastructure.helper.ExceptionHelper;
import com.example.sbt.infrastructure.helper.LocaleHelper;
import com.example.sbt.module.auth.dto.RefreshTokenRequest;
import com.example.sbt.module.auth.dto.RefreshTokenResponse;
import com.example.sbt.module.auth.service.AuthService;
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
    private final LocaleHelper localeHelper;
    private final ExceptionHelper exceptionHelper;
    private final AuthService authService;

    @PostMapping("/token/exchange")
    public ResponseEntity<CommonResponse<RefreshTokenResponse>> exchangeToken(@RequestBody RefreshTokenRequest requestDTO) {
        var result = authService.refreshAccessToken(requestDTO.getToken(), RequestContextHolder.get());
        return ResponseEntity.ok(new CommonResponse<>(result));
    }
}
