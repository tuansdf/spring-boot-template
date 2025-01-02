package org.tuanna.xcloneserver.modules.jwt;

import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;

public interface JWTService {

    String create(JWTPayload payload);

    String createAccessJwt(JWTPayload payload);

    String createRefreshToken(JWTPayload payload);

    JWTPayload verify(String token);

}
