package com.example.demo.module.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.constant.CommonType;
import com.example.demo.constant.Env;
import com.example.demo.constant.PermissionCode;
import com.example.demo.module.jwt.dto.JWTPayload;
import com.example.demo.util.Base64Helper;
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

    private final Env env;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final ObjectMapper objectMapper;

    @Override
    public JWTPayload create(JWTPayload jwtPayload) {
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
        String value = JWT.create()
                .withPayload(jwtPayload.toMap())
                .sign(algorithm);
        jwtPayload.setValue(value);
        return jwtPayload;
    }

    @Override
    public JWTPayload createAccessJwt(UUID userId, Set<String> permissions) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setPermissions(PermissionCode.toIndexes(permissions));
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtAccessLifetime()));
        jwtPayload.setType(CommonType.ACCESS_TOKEN);
        jwtPayload.setValue(create(jwtPayload).getValue());
        return jwtPayload;
    }

    @Override
    public JWTPayload createRefreshJwt(UUID userId, UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setTokenId(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtRefreshLifetime()));
        jwtPayload.setType(CommonType.REFRESH_TOKEN);
        jwtPayload.setValue(create(jwtPayload).getValue());
        return jwtPayload;
    }

    @Override
    public JWTPayload createResetPasswordJwt(UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setTokenId(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtResetPasswordLifetime()));
        jwtPayload.setType(CommonType.RESET_PASSWORD);
        jwtPayload.setValue(create(jwtPayload).getValue());
        return jwtPayload;
    }

    @Override
    public JWTPayload createActivateAccountJwt(UUID tokenId, boolean isReactivate) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setTokenId(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtActivateAccountLifetime()));
        jwtPayload.setType(isReactivate ? CommonType.REACTIVATE_ACCOUNT : CommonType.ACTIVATE_ACCOUNT);
        jwtPayload.setValue(create(jwtPayload).getValue());
        return jwtPayload;
    }

    @Override
    public JWTPayload verify(String token) {
        try {
            String jwtPayloadBase64 = jwtVerifier.verify(token).getPayload();
            String jwtPayloadJson = Base64Helper.urlDecode(jwtPayloadBase64);
            return objectMapper.readValue(jwtPayloadJson, JWTPayload.class);
        } catch (Exception e) {
            log.error("verify", e);
            return null;
        }
    }

}
