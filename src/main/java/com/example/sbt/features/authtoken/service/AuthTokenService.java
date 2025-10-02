package com.example.sbt.features.authtoken.service;

import com.example.sbt.features.authtoken.dto.AuthTokenDTO;
import com.example.sbt.features.authtoken.entity.AuthToken;

import java.util.List;
import java.util.UUID;

public interface AuthTokenService {
    AuthTokenDTO findOneById(UUID id);

    void invalidateByUserIdAndType(UUID userId, AuthToken.Type type);

    void invalidateByUserIdAndTypes(UUID userId, List<AuthToken.Type> types);

    void invalidateByUserId(UUID userId);

    AuthTokenDTO findOneAndVerifyJwt(String jwt, AuthToken.Type type);

    AuthTokenDTO createRefreshToken(UUID userId);

    AuthTokenDTO createOauth2ExchangeToken(UUID userId);

    AuthTokenDTO createResetPasswordToken(UUID userId);

    AuthTokenDTO createActivateAccountToken(UUID userId);
}
