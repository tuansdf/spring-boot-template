package com.example.demo.module.token;

import com.example.demo.common.constant.CommonStatus;
import com.example.demo.common.constant.CommonType;
import com.example.demo.common.mapper.CommonMapper;
import com.example.demo.common.util.RandomUtils;
import com.example.demo.module.jwt.JWTService;
import com.example.demo.module.jwt.dto.JWTPayload;
import com.example.demo.module.token.dto.TokenDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
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
        Optional<Token> result = tokenRepository.findById(id);
        return result.map(commonMapper::toDTO).orElse(null);
    }

    @Override
    public TokenDTO findOneActiveById(UUID id) {
        Optional<Token> token = tokenRepository.findTopByIdAndStatusAndExpiresAtAfter(id, CommonStatus.ACTIVE, Instant.now());
        return token.map(commonMapper::toDTO).orElse(null);

    }

    @Override
    public void deactivatePastTokens(UUID userId, Integer type) {
        tokenRepository.updateStatusByOwnerIdAndTypeAndCreatedAtBefore(userId, type, Instant.now(), CommonStatus.INACTIVE);
    }

    @Override
    public TokenDTO createRefreshToken(UUID userId) {
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createRefreshJwt(userId, id);

        Token token = new Token();
        token.setId(id);
        token.setOwnerId(userId);
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
        token.setOwnerId(userId);
        token.setExpiresAt(jwtPayload.getExpiresAt());
        token.setType(CommonType.RESET_PASSWORD);
        token.setStatus(CommonStatus.ACTIVE);

        TokenDTO result = commonMapper.toDTO(tokenRepository.save(token));
        result.setValue(jwtPayload.getValue());
        return result;
    }

    @Override
    public TokenDTO createActivateAccountToken(UUID userId, boolean isReactivate) {
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(id, isReactivate);

        Token token = new Token();
        token.setId(id);
        token.setOwnerId(userId);
        token.setExpiresAt(jwtPayload.getExpiresAt());
        token.setType(CommonType.ACTIVATE_ACCOUNT);
        token.setStatus(CommonStatus.ACTIVE);

        TokenDTO result = commonMapper.toDTO(tokenRepository.save(token));
        result.setValue(jwtPayload.getValue());
        return result;
    }

}
