package com.example.sbt.module.authtoken.service;

import com.example.sbt.module.authtoken.dto.AuthTokenDTO;

import java.util.List;
import java.util.UUID;

public interface AuthTokenService {
    AuthTokenDTO findOneById(UUID id);

    void invalidateByUserIdAndType(UUID userId, String type);

    void invalidateByUserIdAndTypes(UUID userId, List<String> types);

    void invalidateByUserId(UUID userId);

    AuthTokenDTO findOneAndVerifyJwt(String token, String type);

    AuthTokenDTO createRefreshToken(UUID userId);

    AuthTokenDTO createResetPasswordToken(UUID userId);

    AuthTokenDTO createActivateAccountToken(UUID userId);
}
