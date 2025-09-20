package com.example.sbt.module.authtoken.service;

import com.example.sbt.common.constant.ApplicationProperties;
import com.example.sbt.common.dto.JWTPayload;
import com.example.sbt.infrastructure.helper.JWTHelper;
import com.example.sbt.module.authtoken.entity.AuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class JWTServiceImpl implements JWTService {
    private final ApplicationProperties applicationProperties;
    private final JWTHelper jwtHelper;

    @Override
    public JWTPayload createAccessJwt(UUID userId, List<String> permissions) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setPermissions(permissions);
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtAccessLifetime()));
        jwtPayload.setType(AuthToken.Type.ACCESS_TOKEN);
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createRefreshJwt(UUID userId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtRefreshLifetime()));
        jwtPayload.setType(AuthToken.Type.REFRESH_TOKEN);
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createResetPasswordJwt(UUID userId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtResetPasswordLifetime()));
        jwtPayload.setType(AuthToken.Type.RESET_PASSWORD);
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createActivateAccountJwt(UUID userId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(userId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtActivateAccountLifetime()));
        jwtPayload.setType(AuthToken.Type.ACTIVATE_ACCOUNT);
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload verify(String token) {
        return jwtHelper.verify(token);
    }
}
