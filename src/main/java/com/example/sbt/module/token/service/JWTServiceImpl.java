package com.example.sbt.module.token.service;

import com.example.sbt.core.constant.ApplicationProperties;
import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.constant.PermissionCode;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.helper.JWTHelper;
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
        jwtPayload.setPermissions(PermissionCode.toIndexes(permissions));
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtAccessLifetime()));
        jwtPayload.setType(CommonType.toIndex(CommonType.ACCESS_TOKEN));
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
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
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
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
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload createActivateAccountJwt(UUID tokenId) {
        Instant now = Instant.now();
        JWTPayload jwtPayload = new JWTPayload();
        jwtPayload.setSubject(tokenId.toString());
        jwtPayload.setIssuedAt(now);
        jwtPayload.setNotBefore(now);
        jwtPayload.setExpiresAt(now.plusSeconds(applicationProperties.getJwtActivateAccountLifetime()));
        jwtPayload.setType(CommonType.toIndex(CommonType.ACTIVATE_ACCOUNT));
        jwtPayload.setValue(jwtHelper.create(jwtPayload));
        return jwtPayload;
    }

    @Override
    public JWTPayload verify(String token) {
        return jwtHelper.verify(token);
    }

}
