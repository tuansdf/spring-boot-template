package org.tuanna.xcloneserver.modules.token;

import org.tuanna.xcloneserver.modules.token.dtos.TokenDTO;

import java.util.UUID;

public interface TokenService {

    TokenDTO findOneById(UUID id);

    TokenDTO findOneValidatedById(UUID id, String type);

    void deactivatePastToken(UUID ownerId, String type);

    TokenDTO createRefreshToken(UUID userId);

    TokenDTO createResetPasswordToken(UUID userId);

    TokenDTO createActivateAccountToken(UUID actionBy);

}
