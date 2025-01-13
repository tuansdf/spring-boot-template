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
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.utils.Base64Utils;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
    public JWTPayload createAccessJwt(UUID userId, List<String> permissions) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubjectId(userId.toString());
        jwtPayload.setPermissions(PermissionCode.toIndexes(permissions));
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtAccessLifetime()));
        jwtPayload.setType(TokenType.toIndex(TokenType.ACCESS_TOKEN));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createRefreshJwt(UUID userId, UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubjectId(userId.toString());
        jwtPayload.setTokenId(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtRefreshLifetime()));
        jwtPayload.setType(TokenType.toIndex(TokenType.REFRESH_TOKEN));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createResetPasswordJwt(UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(UUIDUtils.generate().toString());
        jwtPayload.setTokenId(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtResetPasswordLifetime()));
        jwtPayload.setType(TokenType.toIndex(TokenType.RESET_PASSWORD));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createActivateAccountJwt(UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(UUIDUtils.generate().toString());
        jwtPayload.setTokenId(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(env.getJwtActivateAccountLifetime()));
        jwtPayload.setType(TokenType.toIndex(TokenType.ACTIVATE_ACCOUNT));
        jwtPayload.setValue(create(jwtPayload));
        return jwtPayload;
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
