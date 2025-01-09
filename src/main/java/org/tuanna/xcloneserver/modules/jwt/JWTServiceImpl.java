package org.tuanna.xcloneserver.modules.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.Env;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.utils.Base64Utils;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class JWTServiceImpl implements JWTService {

    private final Env env;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    @Override
    public String create(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        if (jwtPayload.getIssuedAt() == null) {
            jwtPayload.setIssuedAt(now);
        }
        if (jwtPayload.getNotBefore() == null) {
            jwtPayload.setNotBefore(now);
        }
        if (jwtPayload.getExpiresAt() == null) {
            jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtAccessLifetime()));
        }
        return JWT.create()
                .withPayload(jwtPayload.toMap())
                .sign(algorithm);
    }

    @Override
    public String createAccessJwt(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtAccessLifetime()));
        jwtPayload.setType(TokenType.toIndex(TokenType.ACCESS_TOKEN));
        return create(jwtPayload);
    }

    @Override
    public String createRefreshToken(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtRefreshLifetime()));
        jwtPayload.setType(TokenType.toIndex(TokenType.REFRESH_TOKEN));
        return create(jwtPayload);
    }

    @Override
    public JWTPayload verify(String token) {
        try {
            return objectMapper.readValue(Base64Utils.decodeUrl(jwtVerifier.verify(token).getPayload()), JWTPayload.class);
        } catch (Exception e) {
            log.error("verify", e);
            return null;
        }
    }

}
