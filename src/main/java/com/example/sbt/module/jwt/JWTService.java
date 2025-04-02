package com.example.sbt.module.jwt;

import com.example.sbt.module.jwt.dto.JWTPayload;

import java.util.Set;
import java.util.UUID;

public interface JWTService {

    JWTPayload create(JWTPayload payload);

    JWTPayload createAccessJwt(UUID userId, Set<String> permissions);

    JWTPayload createRefreshJwt(UUID tokenId);

    JWTPayload createResetPasswordJwt(UUID tokenId);

    JWTPayload createActivateAccountJwt(UUID tokenId, boolean isReactivate);

    JWTPayload verify(String token);

}
