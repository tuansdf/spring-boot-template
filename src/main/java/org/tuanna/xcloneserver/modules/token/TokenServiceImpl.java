package org.tuanna.xcloneserver.modules.token;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.Status;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.entities.Token;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.utils.ConversionUtils;
import org.tuanna.xcloneserver.utils.DateUtils;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final JWTService jwtService;

    @Override
    public boolean validateTokenById(UUID id, String value, String type) {
        if (id == null) {
            return false;
        }

        Optional<Token> tokenOptional = tokenRepository.findById(id);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        Token token = tokenOptional.get();
        boolean isTypeCorrect = !StringUtils.isEmpty(token.getType()) && token.getType().equals(type);
        boolean isValueCorrect = !StringUtils.isEmpty(token.getValue()) && token.getValue().equals(value);
        boolean isActive = Status.ACTIVE.equals(token.getStatus());
        boolean isExpired = token.getExpiresAt().isAfter(ZonedDateTime.now());
        return isTypeCorrect && isValueCorrect && isActive && isExpired;
    }

    @Override
    public Token createRefreshJwt(JWTPayload jwtPayload) {
        UUID id = UUIDUtils.generateId();
        jwtPayload.setTokenId(id.toString());
        String jwt = jwtService.createRefreshToken(jwtPayload);

        Token token = new Token();
        token.setId(id);
        token.setExpiresAt(DateUtils.toZonedDateTime(jwtPayload.getExpiresAt()));
        token.setType(TokenType.REFRESH_TOKEN);
        token.setOwnerId(ConversionUtils.toUUID(jwtPayload.getSubjectId()));
        token.setValue(jwt);
        token.setStatus(Status.ACTIVE);
        return tokenRepository.save(token);
    }

}
