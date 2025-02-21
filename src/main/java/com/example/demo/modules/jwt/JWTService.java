package com.example.demo.modules.jwt;

import com.example.demo.modules.jwt.dtos.JWTPayload;

import java.util.Set;
import java.util.UUID;

public interface JWTService {

    JWTPayload create(JWTPayload payload);

    JWTPayload createAccessJwt(UUID userId, Set<String> permissions);

    JWTPayload createRefreshJwt(UUID userId, UUID tokenId);

    JWTPayload createResetPasswordJwt(UUID tokenId);

    JWTPayload createActivateAccountJwt(UUID tokenId, boolean isReactivate);

    JWTPayload verify(String token);

}
