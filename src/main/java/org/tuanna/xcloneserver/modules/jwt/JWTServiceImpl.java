package org.tuanna.xcloneserver.modules.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.Envs;
import org.tuanna.xcloneserver.constants.TokenTypes;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.utils.Base64Utils;

import java.time.Instant;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class JWTServiceImpl implements JWTService {

    private final Envs envs;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    @Override
    public String create(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        Map<String, Object> payload = objectMapper.convertValue(jwtPayload, new TypeReference<Map<String, Object>>() {
        });
        if (jwtPayload.getIssuedAt() == null) {
            jwtPayload.setIssuedAt(now);
        }
        if (jwtPayload.getNotBefore() == null) {
            jwtPayload.setNotBefore(now);
        }
        if (jwtPayload.getExpiresAt() == null) {
            jwtPayload.setExpiresAt(now.plusSeconds(envs.getJwtAccessLifetime()));
        }
        payload.put("iat", jwtPayload.getIssuedAt());
        payload.put("nbf", jwtPayload.getNotBefore());
        payload.put("exp", jwtPayload.getExpiresAt());
        return JWT.create()
                .withPayload(payload)
                .sign(algorithm);
    }

    @Override
    public String createAccessToken(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(envs.getJwtAccessLifetime()));
        jwtPayload.setType(TokenTypes.REFRESH);
        return create(jwtPayload);
    }

    @Override
    public String createRefreshToken(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(envs.getJwtRefreshLifetime()));
        jwtPayload.setType(TokenTypes.REFRESH);
        return create(jwtPayload);
    }

    @Override
    public JWTPayload verify(String token) {
        try {
            return objectMapper.readValue(Base64Utils.decodeUrl(jwtVerifier.verify(token).getPayload()), JWTPayload.class);
        } catch (Exception e) {
            log.error("jwtservice verify", e);
            return null;
        }
    }

}
