package com.example.sbt.module.authtoken.service;

import com.example.sbt.module.authtoken.dto.AuthTokenDTO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.UUID;

public interface AuthTokenService {
    AuthTokenDTO findOneById(UUID id);

    void deactivateByUserIdAndType(UUID userId, String type);

    void deactivateByUserIdAndTypes(UUID userId, List<String> types);

    void deactivateByUserId(UUID userId);

    void deleteExpiredTokens();

    @Async
    void deleteExpiredTokensAsync();

    AuthTokenDTO findOneAndVerifyJwt(String token, String type);

    AuthTokenDTO createRefreshToken(UUID userId);

    AuthTokenDTO createResetPasswordToken(UUID userId);

    AuthTokenDTO createActivateAccountToken(UUID userId);
}
