package com.example.demo.modules.token;

import com.example.demo.constants.CommonStatus;
import com.example.demo.constants.CommonType;
import com.example.demo.entities.Token;
import com.example.demo.mappers.CommonMapper;
import com.example.demo.modules.jwt.JWTService;
import com.example.demo.modules.jwt.dtos.JWTPayload;
import com.example.demo.modules.token.dtos.TokenDTO;
import com.example.demo.utils.DateUtils;
import com.example.demo.utils.RandomUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
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
        if (id == null) {
            return null;
        }

        TokenDTO token = findOneById(id);
        if (token == null) return null;

        boolean isActive = CommonStatus.ACTIVE.equals(token.getStatus());
        if (!isActive) return null;

        boolean isExpired = OffsetDateTime.now().isAfter(token.getExpiresAt());
        if (isExpired) return null;

        return token;
    }

    @Override
    public void deactivatePastTokens(UUID userId, String type) {
        tokenRepository.updateStatusByOwnerIdAndTypeAndCreatedAtBefore(userId, type, OffsetDateTime.now(), CommonStatus.INACTIVE);
    }

    @Override
    public TokenDTO createRefreshToken(UUID userId) {
        UUID id = RandomUtils.Secure.generateTimeBasedUUID();
        JWTPayload jwtPayload = jwtService.createRefreshJwt(userId, id);

        Token token = new Token();
        token.setId(id);
        token.setOwnerId(userId);
        token.setExpiresAt(DateUtils.toOffsetDateTime(jwtPayload.getExpiresAt()));
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
        token.setExpiresAt(DateUtils.toOffsetDateTime(jwtPayload.getExpiresAt()));
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
        token.setExpiresAt(DateUtils.toOffsetDateTime(jwtPayload.getExpiresAt()));
        token.setType(CommonType.ACTIVATE_ACCOUNT);
        token.setStatus(CommonStatus.ACTIVE);

        TokenDTO result = commonMapper.toDTO(tokenRepository.save(token));
        result.setValue(jwtPayload.getValue());
        return result;
    }

}
