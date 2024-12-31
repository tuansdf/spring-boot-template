package org.tuanna.xcloneserver.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.dtos.JwtPayload;
import org.tuanna.xcloneserver.services.JwtService;
import org.tuanna.xcloneserver.utils.Base64Utils;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtServiceImpl implements JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;

    @Override
    public String create(JwtPayload payload) {
        return JWT.create()
                .withExpiresAt(payload.getExpiresAt())
                .withIssuedAt(payload.getIssuedAt())
                .withNotBefore(payload.getNotBefore())
                .withIssuer(payload.getIssuer())
                .withSubject(payload.getSubject())
                .withPayload(payload.getClaims())
                .sign(algorithm);
    }

    @Override
    public DecodedJWT verify(String token) {
        try {
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            log.info(Base64Utils.decodeUrl(decodedJWT.getPayload()));
            return decodedJWT;
        } catch (Exception e) {
            return null;
        }
    }

}
