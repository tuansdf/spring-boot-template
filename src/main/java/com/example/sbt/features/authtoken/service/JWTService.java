package com.example.sbt.features.authtoken.service;

import com.example.sbt.common.dto.JWTPayload;

import java.util.List;
import java.util.UUID;

public interface JWTService {
    JWTPayload createOauth2ExchangeJwt(UUID userId);

    JWTPayload createAccessJwt(UUID userId, List<String> permissions);

    JWTPayload createRefreshJwt(UUID tokenId);

    JWTPayload createResetPasswordJwt(UUID tokenId);

    JWTPayload createActivateAccountJwt(UUID tokenId);

    JWTPayload verify(String token);
}
