package org.tuanna.xcloneserver.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.tuanna.xcloneserver.dtos.JwtPayload;

public interface JwtService {
    String create(JwtPayload payload);

    DecodedJWT verify(String token);
}
