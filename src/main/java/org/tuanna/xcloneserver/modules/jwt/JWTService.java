package org.tuanna.xcloneserver.modules.jwt;

import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;

import java.util.List;
import java.util.UUID;

public interface JWTService {

    String create(JWTPayload payload);

    JWTPayload createAccessJwt(UUID userId, List<String> permissions);

    JWTPayload createRefreshJwt(UUID userId, UUID tokenId);

    JWTPayload createResetPasswordJwt(UUID tokenId);

    JWTPayload createActivateAccountJwt(UUID tokenId);

    JWTPayload verify(String token);

}
