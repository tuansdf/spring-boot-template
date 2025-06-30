package com.example.sbt.module.jwt;

import com.example.sbt.module.jwt.dto.JWTPayload;

import java.util.List;
import java.util.UUID;

public interface JWTService {

    JWTPayload createAccessJwt(UUID userId, List<String> permissions);

    JWTPayload createRefreshJwt(UUID tokenId);

    JWTPayload createResetPasswordJwt(UUID tokenId);

    JWTPayload createActivateAccountJwt(UUID tokenId);

    JWTPayload verify(String token);

}
