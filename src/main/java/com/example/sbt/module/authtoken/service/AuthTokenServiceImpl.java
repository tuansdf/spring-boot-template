package com.example.sbt.module.authtoken.service;

import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.mapper.CommonMapper;
import com.example.sbt.module.authtoken.dto.AuthTokenDTO;
import com.example.sbt.module.authtoken.entity.AuthToken;
import com.example.sbt.module.authtoken.repository.AuthTokenRepository;
import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.shared.util.RandomUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    @Override
    public AuthTokenDTO findOneById(UUID id) {
        if (id == null) return null;
        return authTokenRepository.findById(id).map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public void deactivateByUserIdAndType(UUID userId, String type) {
        authTokenRepository.updateStatusByUserIdAndType(userId, type, CommonStatus.INACTIVE);
    }

    @Override
    public void deactivateByUserIdAndTypes(UUID userId, List<String> types) {
        authTokenRepository.updateStatusByUserIdAndTypes(userId, types, CommonStatus.INACTIVE);
    }

    @Override
    public void deactivateByUserId(UUID userId) {
        authTokenRepository.updateStatusByUserId(userId, CommonStatus.INACTIVE);
    }

    @Override
    public void deleteExpiredTokens() {
        authTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }

    @Async
    @Override
    public void deleteExpiredTokensAsync() {
        deleteExpiredTokens();
    }

    @Override
    public AuthTokenDTO findOneAndVerifyJwt(String jwt, String type) {
        if (StringUtils.isBlank(jwt) || StringUtils.isBlank(type)) {
            return null;
        }
        JWTPayload jwtPayload = jwtService.verify(jwt);
        if (jwtPayload == null) {
            return null;
        }
        String tokenType = CommonType.fromIndex(jwtPayload.getType());
        if (!type.equals(tokenType)) {
            return null;
        }
        UUID tokenId = ConversionUtils.toUUID(jwtPayload.getSubject());
        AuthTokenDTO authTokenDTO = findOneById(tokenId);
        if (authTokenDTO == null) {
            return null;
        }
        if (!type.equals(authTokenDTO.getType()) || !CommonStatus.ACTIVE.equals(authTokenDTO.getStatus())) {
            return null;
        }
        if (Instant.now().isAfter(authTokenDTO.getExpiresAt())) {
            return null;
        }
        return authTokenDTO;
    }

    @Override
    public AuthTokenDTO createRefreshToken(UUID userId) {
        UUID id = RandomUtils.secure().randomTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createRefreshJwt(id);

        AuthToken authToken = new AuthToken();
        authToken.setId(id);
        authToken.setUserId(userId);
        authToken.setExpiresAt(jwtPayload.getExpiresAt());
        authToken.setType(CommonType.REFRESH_TOKEN);
        authToken.setStatus(CommonStatus.ACTIVE);

        AuthTokenDTO result = commonMapper.toDTO(authTokenRepository.save(authToken));
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public AuthTokenDTO createResetPasswordToken(UUID userId) {
        UUID id = RandomUtils.secure().randomTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createResetPasswordJwt(id);

        AuthToken authToken = new AuthToken();
        authToken.setId(id);
        authToken.setUserId(userId);
        authToken.setExpiresAt(jwtPayload.getExpiresAt());
        authToken.setType(CommonType.RESET_PASSWORD);
        authToken.setStatus(CommonStatus.ACTIVE);

        AuthTokenDTO result = commonMapper.toDTO(authTokenRepository.save(authToken));
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public AuthTokenDTO createActivateAccountToken(UUID userId) {
        UUID id = RandomUtils.secure().randomTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(id);

        AuthToken authToken = new AuthToken();
        authToken.setId(id);
        authToken.setUserId(userId);
        authToken.setExpiresAt(jwtPayload.getExpiresAt());
        authToken.setType(CommonType.ACTIVATE_ACCOUNT);
        authToken.setStatus(CommonStatus.ACTIVE);

        AuthTokenDTO result = commonMapper.toDTO(authTokenRepository.save(authToken));
        result.setValue(jwtPayload.getValue());
        return result;
    }
}
