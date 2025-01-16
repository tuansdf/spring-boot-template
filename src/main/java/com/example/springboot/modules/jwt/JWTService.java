package com.example.springboot.modules.jwt;

import com.example.springboot.modules.jwt.dtos.JWTPayload;

import java.util.List;
import java.util.UUID;

public interface JWTService {

    JWTPayload create(JWTPayload payload);

    JWTPayload createAccessJwt(UUID userId, List<String> permissions);

    JWTPayload createRefreshJwt(UUID userId, UUID tokenId);

    JWTPayload createResetPasswordJwt(UUID tokenId);

    JWTPayload createActivateAccountJwt(UUID tokenId);

    JWTPayload verify(String token);

}
