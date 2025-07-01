package com.example.sbt.core.helper;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.sbt.common.util.Base64Utils;
import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.core.dto.JWTPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class JWTHelper {

    private final ApplicationProperties applicationProperties;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    public String create(JWTPayload jwtPayload) {
        Instant now = Instant.now();
        if (jwtPayload == null) {
            jwtPayload = new JWTPayload();
        }
        if (jwtPayload.getIssuedAt() == null) {
            jwtPayload.setIssuedAt(now);
        }
        if (jwtPayload.getNotBefore() == null) {
            jwtPayload.setNotBefore(now);
        }
        if (jwtPayload.getExpiresAt() == null) {
            jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtAccessLifetime()));
        }
        return JWT.create()
                .withPayload(jwtPayload.toMap())
                .sign(algorithm);
    }

    public JWTPayload verify(String token) {
        try {
            String jwtPayloadBase64 = jwtVerifier.verify(token).getPayload();
            String jwtPayloadJson = Base64Utils.urlDecodeToString(jwtPayloadBase64);
            return objectMapper.readValue(jwtPayloadJson, JWTPayload.class);
        } catch (Exception e) {
            log.error("verify ", e);
            return null;
        }
    }

}
