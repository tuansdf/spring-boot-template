package org.tuanna.xcloneserver.modules.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.tuanna.xcloneserver.modules.jwt.dtos.JwtPayload;

public interface JwtService {

    String createAccessToken(JwtPayload payload);

    DecodedJWT verify(String token);

}
