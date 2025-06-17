package com.example.sbt.module.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.constant.CommonType;
import com.example.sbt.common.constant.PermissionCode;
import com.example.sbt.common.util.Base64Helper;
import com.example.sbt.module.jwt.dto.JWTPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JWTServiceImpl implements JWTService {

    private final ApplicationProperties applicationProperties;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    private String create(JWTPayload jwtPayload) {
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

    @Override
    public JWTPayload createAccessJwt(UUID userId, Set<String> permissions) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setPermissions(PermissionCode.toIndexes(permissions));
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtAccessLifetime()));
        jwtPayload.setType(CommonType.toIndex(CommonType.ACCESS_TOKEN));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createRefreshJwt(UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtRefreshLifetime()));
        jwtPayload.setType(CommonType.toIndex(CommonType.REFRESH_TOKEN));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createResetPasswordJwt(UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtResetPasswordLifetime()));
        jwtPayload.setType(CommonType.toIndex(CommonType.RESET_PASSWORD));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createActivateAccountJwt(UUID tokenId, boolean isReactivate) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtActivateAccountLifetime()));
        jwtPayload.setType(CommonType.toIndex(isReactivate ? CommonType.REACTIVATE_ACCOUNT : CommonType.ACTIVATE_ACCOUNT));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload verify(String token) {
        try {
            String jwtPayloadBase64 = jwtVerifier.verify(token).getPayload();
            String jwtPayloadJson = Base64Helper.urlDecodeToString(jwtPayloadBase64);
            return objectMapper.readValue(jwtPayloadJson, JWTPayload.class);
        } catch (Exception e) {
            log.error("verify ", e);
            return null;
        }
    }

}
