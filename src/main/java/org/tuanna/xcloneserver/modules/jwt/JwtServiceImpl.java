package org.tuanna.xcloneserver.modules.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.modules.jwt.dtos.JwtPayload;
import org.tuanna.xcloneserver.utils.Base64Utils;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final Algorithm algorithm;
    private final JwtParser jwtVerifier;
    private final ObjectMapper objectMapper;

    @Override
    public String createAccessToken(JwtPayload payload) {
        JWTCreator.Builder builder = JWT.create();
        return builder.sign(algorithm);
    }

    @Override
    public JwtPayload verify(String token) {
        try {
            DecodedJWT decodedJWT = jwtVerifier.parse(token).getPayload();
            log.info("Jackson: {}", objectMapper.readValue(Base64Utils.decodeUrl(decodedJWT.getPayload()), AccessJwtPayload.class));
            return decodedJWT;
        } catch (Exception e) {
            log.error("jackson", e);
            return null;
        }
    }

}
