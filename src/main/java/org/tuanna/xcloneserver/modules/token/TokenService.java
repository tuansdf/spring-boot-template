package org.tuanna.xcloneserver.modules.token;

import org.tuanna.xcloneserver.entities.Token;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;

import java.util.UUID;

public interface TokenService {

    boolean validateToken(UUID id, String value, String type);

    Token createRefreshJwt(JWTPayload jwtPayload);
}
