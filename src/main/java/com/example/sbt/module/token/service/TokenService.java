package com.example.sbt.module.token.service;

import com.example.sbt.module.token.dto.TokenDTO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.UUID;

public interface TokenService {

    TokenDTO findOneById(UUID id);

    void deactivateByUserIdAndType(UUID userId, String type);

    void deactivateByUserIdAndTypes(UUID userId, List<String> types);

    void deactivateByUserId(UUID userId);

    void deleteExpiredTokens();

    @Async
    void deleteExpiredTokensAsync();

    TokenDTO findOneAndVerifyJwt(String token, String type);

    TokenDTO createRefreshToken(UUID userId);

    TokenDTO createResetPasswordToken(UUID userId);

    TokenDTO createActivateAccountToken(UUID userId);

}
