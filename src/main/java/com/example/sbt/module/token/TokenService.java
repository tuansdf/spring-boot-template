package com.example.sbt.module.token;

import com.example.sbt.module.token.dto.TokenDTO;

import java.util.List;
import java.util.UUID;

public interface TokenService {

    TokenDTO findOneById(UUID id);

    void deactivateByUserIdAndType(UUID userId, String type);

    void deactivateByUserIdAndTypes(UUID userId, List<String> types);

    void deactivateByUserId(UUID userId);

    TokenDTO findOneAndVerifyJwt(String token, String type);

    TokenDTO createRefreshToken(UUID userId);

    TokenDTO createResetPasswordToken(UUID userId);

    TokenDTO createActivateAccountToken(UUID userId);

}
