package com.example.sbt.module.authtoken.service;

import com.example.sbt.common.dto.JWTPayload;
import com.example.sbt.common.mapper.CommonMapper;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.module.authtoken.dto.AuthTokenDTO;
import com.example.sbt.module.authtoken.entity.AuthToken;
import com.example.sbt.module.authtoken.repository.AuthTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
public class AuthTokenServiceImpl implements AuthTokenService {
    private final CommonMapper commonMapper;
    private final AuthTokenRepository authTokenRepository;
    private final JWTService jwtService;

    private AuthTokenDTO findOneOrCreateByUserIdAndType(UUID userId, AuthToken.Type type) {
        Instant now = Instant.now();
        AuthTokenDTO result = authTokenRepository.findTopByUserIdAndType(userId, type).map(commonMapper::toDTO).orElse(null);
        if (result != null) return result;
        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);
        authToken.setValidFrom(now);
        authToken.setType(type);
        return commonMapper.toDTO(authTokenRepository.save(authToken));
    }

    private AuthTokenDTO findOneByUserIdAndType(UUID userId, AuthToken.Type type) {
        if (userId == null || type == null) return null;
        return authTokenRepository.findTopByUserIdAndType(userId, type).map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public AuthTokenDTO findOneById(UUID id) {
        if (id == null) return null;
        return authTokenRepository.findById(id).map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public void invalidateByUserIdAndType(UUID userId, AuthToken.Type type) {
        authTokenRepository.invalidateByUserIdAndType(userId, type);
    }

    @Override
    public void invalidateByUserIdAndTypes(UUID userId, List<AuthToken.Type> types) {
        authTokenRepository.invalidateByUserIdAndTypes(userId, types);
    }

    @Override
    public void invalidateByUserId(UUID userId) {
        authTokenRepository.invalidateByUserId(userId);
    }

    @Override
    public AuthTokenDTO findOneAndVerifyJwt(String jwt, AuthToken.Type type) {
        if (StringUtils.isBlank(jwt) || type == null) {
            return null;
        }
        JWTPayload jwtPayload = jwtService.verify(jwt);
        if (jwtPayload == null || !type.equals(jwtPayload.getType())) {
            return null;
        }
        UUID userId = ConversionUtils.toUUID(jwtPayload.getSubject());
        AuthTokenDTO authTokenDTO = findOneByUserIdAndType(userId, type);
        if (authTokenDTO == null) {
            return null;
        }
        if (authTokenDTO.getValidFrom().truncatedTo(ChronoUnit.SECONDS).isAfter(jwtPayload.getIssuedAt())) {
            return null;
        }
        return authTokenDTO;
    }

    @Override
    public AuthTokenDTO createRefreshToken(UUID userId) {
        AuthTokenDTO result = findOneOrCreateByUserIdAndType(userId, AuthToken.Type.REFRESH_TOKEN);
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(userId);
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public AuthTokenDTO createResetPasswordToken(UUID userId) {
        AuthTokenDTO result = findOneOrCreateByUserIdAndType(userId, AuthToken.Type.RESET_PASSWORD);
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(userId);
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public AuthTokenDTO createActivateAccountToken(UUID userId) {
        AuthTokenDTO result = findOneOrCreateByUserIdAndType(userId, AuthToken.Type.ACTIVATE_ACCOUNT);
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(userId);
        result.setValue(jwtPayload.getValue());
        return result;
    }
}
