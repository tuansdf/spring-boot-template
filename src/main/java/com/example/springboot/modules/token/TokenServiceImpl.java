package com.example.springboot.modules.token;

import com.example.springboot.constants.CommonStatus;
import com.example.springboot.constants.CommonType;
import com.example.springboot.entities.Token;
import com.example.springboot.mappers.CommonMapper;
import com.example.springboot.modules.jwt.JWTService;
import com.example.springboot.modules.jwt.dtos.JWTPayload;
import com.example.springboot.modules.token.dtos.TokenDTO;
import com.example.springboot.utils.DateUtils;
import com.example.springboot.utils.UUIDUtils;
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
        UUID id = UUIDUtils.generateId();
        JWTPayload jwtPayload = jwtService.createRefreshJwt(userId, id);

        Token token = new Token();
        token.setId(id);
        token.setExpiresAt(DateUtils.toOffsetDateTime(jwtPayload.getExpiresAt()));
        token.setType(CommonType.REFRESH_TOKEN);
        token.setOwnerId(userId);
        token.setValue(jwtPayload.getValue());
        token.setStatus(CommonStatus.ACTIVE);
        token.setCreatedBy(userId);
        token.setUpdatedBy(userId);
        return commonMapper.toDTO(tokenRepository.save(token));
    }

    @Override
    public TokenDTO createResetPasswordToken(UUID userId) {
        UUID id = UUIDUtils.generateId();
        JWTPayload jwtPayload = jwtService.createResetPasswordJwt(id);

        Token token = new Token();
        token.setId(id);
        token.setExpiresAt(DateUtils.toOffsetDateTime(jwtPayload.getExpiresAt()));
        token.setType(CommonType.RESET_PASSWORD);
        token.setOwnerId(userId);
        token.setValue(jwtPayload.getValue());
        token.setStatus(CommonStatus.ACTIVE);
        token.setCreatedBy(userId);
        token.setUpdatedBy(userId);
        return commonMapper.toDTO(tokenRepository.save(token));
    }

    @Override
    public TokenDTO createActivateAccountToken(UUID actionBy) {
        UUID id = UUIDUtils.generateId();
        JWTPayload jwtPayload = jwtService.createActivateAccountJwt(id);

        Token token = new Token();
        token.setId(id);
        token.setExpiresAt(DateUtils.toOffsetDateTime(jwtPayload.getExpiresAt()));
        token.setType(CommonType.ACTIVATE_ACCOUNT);
        token.setOwnerId(actionBy);
        token.setValue(jwtPayload.getValue());
        token.setStatus(CommonStatus.ACTIVE);
        token.setCreatedBy(actionBy);
        token.setUpdatedBy(actionBy);
        return commonMapper.toDTO(tokenRepository.save(token));
    }

}
