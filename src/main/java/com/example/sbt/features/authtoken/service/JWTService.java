package com.example.sbt.features.authtoken.service;

import com.example.sbt.infrastructure.security.JWTPayload;

import java.util.List;
import java.util.UUID;

public interface JWTService {
    String createOauth2ExchangeJwt(UUID userId);

    String createAccessJwt(UUID userId, List<String> permissions);

    String createRefreshJwt(UUID tokenId);

    String createResetPasswordJwt(UUID tokenId);

    String createActivateAccountJwt(UUID tokenId);

    JWTPayload verify(String token);
}
