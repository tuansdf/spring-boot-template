package com.example.sbt.module.token.service;

import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.RandomUtils;
import com.example.sbt.core.constant.CommonStatus;
import com.example.sbt.core.constant.CommonType;
import com.example.sbt.core.dto.JWTPayload;
import com.example.sbt.core.mapper.CommonMapper;
import com.example.sbt.module.token.dto.TokenDTO;
import com.example.sbt.module.token.entity.Token;
import com.example.sbt.module.token.repository.TokenRepository;
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
public class TokenServiceImpl implements TokenService {

    private final CommonMapper commonMapper;
    private final TokenRepository tokenRepository;
    private final JWTService jwtService;

    @Override
    public TokenDTO findOneById(UUID id) {
        if (id == null) return null;
        return tokenRepository.findById(id).map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public void deactivateByUserIdAndType(UUID userId, String type) {
        tokenRepository.updateStatusByUserIdAndType(userId, type, CommonStatus.INACTIVE);
    }

    @Override
    public void deactivateByUserIdAndTypes(UUID userId, List<String> types) {
        tokenRepository.updateStatusByUserIdAndTypes(userId, types, CommonStatus.INACTIVE);
    }

    @Override
    public void deactivateByUserId(UUID userId) {
        tokenRepository.updateStatusByUserId(userId, CommonStatus.INACTIVE);
    }

    @Override
    public void deleteExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(Instant.now());
    }

    @Async
    @Override
    public void deleteExpiredTokensAsync() {
        deleteExpiredTokens();
    }

    @Override
    public TokenDTO findOneAndVerifyJwt(String jwt, String type) {
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
        TokenDTO tokenDTO = findOneById(tokenId);
        if (tokenDTO == null) {
            return null;
        }
        if (!type.equals(tokenDTO.getType()) || !CommonStatus.ACTIVE.equals(tokenDTO.getStatus())) {
            return null;
        }
        if (Instant.now().isAfter(tokenDTO.getExpiresAt())) {
            return null;
        }
        return tokenDTO;
    }

    @Override
    public TokenDTO createRefreshToken(UUID userId) {
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createRefreshJwt(id);

        Token token = new Token();
        token.setId(id);
        token.setUserId(userId);
        token.setExpiresAt(jwtPayload.getExpiresAt());
        token.setType(CommonType.REFRESH_TOKEN);
        token.setStatus(CommonStatus.ACTIVE);

        TokenDTO result = commonMapper.toDTO(tokenRepository.save(token));
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public TokenDTO createResetPasswordToken(UUID userId) {
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createResetPasswordJwt(id);

        Token token = new Token();
        token.setId(id);
        token.setUserId(userId);
        token.setExpiresAt(jwtPayload.getExpiresAt());
        token.setType(CommonType.RESET_PASSWORD);
        token.setStatus(CommonStatus.ACTIVE);

        TokenDTO result = commonMapper.toDTO(tokenRepository.save(token));
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public TokenDTO createActivateAccountToken(UUID userId) {
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(id);

        Token token = new Token();
        token.setId(id);
        token.setUserId(userId);
        token.setExpiresAt(jwtPayload.getExpiresAt());
        token.setType(CommonType.ACTIVATE_ACCOUNT);
        token.setStatus(CommonStatus.ACTIVE);

        TokenDTO result = commonMapper.toDTO(tokenRepository.save(token));
        result.setValue(jwtPayload.getValue());
        return result;
    }

}
