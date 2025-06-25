package com.example.sbt.module.token;

import com.example.sbt.module.token.dto.TokenDTO;

import java.util.List;
import java.util.UUID;

public interface TokenService {

    TokenDTO findOneById(UUID id);

    TokenDTO findOneActiveById(UUID id);

    void deactivatePastTokens(UUID userId, String type);

    void deactivatePastTokens(UUID userId, List<String> types);

    void deactivatePastTokens(UUID userId);

    TokenDTO createRefreshToken(UUID userId);

    TokenDTO createResetPasswordToken(UUID userId);

    TokenDTO createActivateAccountToken(UUID userId);

}
