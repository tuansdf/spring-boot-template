package com.example.springboot.modules.token;

import com.example.springboot.modules.token.dtos.TokenDTO;

import java.util.UUID;

public interface TokenService {

    TokenDTO findOneById(UUID id);

    TokenDTO findOneActiveById(UUID id);

    void deactivatePastTokens(UUID userId, String type);

    TokenDTO createRefreshToken(UUID userId);

    TokenDTO createResetPasswordToken(UUID userId);

    TokenDTO createActivateAccountToken(UUID userId, boolean isReactivate);

}
