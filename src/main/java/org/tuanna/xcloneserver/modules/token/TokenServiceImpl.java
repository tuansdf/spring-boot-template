package org.tuanna.xcloneserver.modules.token;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.constants.CommonStatus;
import org.tuanna.xcloneserver.constants.Envs;
import org.tuanna.xcloneserver.constants.TokenType;
import org.tuanna.xcloneserver.entities.Token;
import org.tuanna.xcloneserver.modules.jwt.JWTService;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.utils.CommonUtils;
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
    private final Envs envs;

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
        boolean isTypeCorrect = !Strings.isNullOrEmpty(token.getType()) && token.getType().equals(type);
        boolean isValueCorrect = !Strings.isNullOrEmpty(token.getValue()) && token.getValue().equals(value);
        boolean isActive = CommonStatus.ACTIVE.equals(token.getStatus());
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
        token.setExpiresAt(DateUtils.convertInstantToZonedDateTime(jwtPayload.getExpiresAt()));
        token.setType(TokenType.REFRESH_TOKEN);
        token.setOwnerId(CommonUtils.safeToUUID(jwtPayload.getSubjectId()));
        token.setValue(jwt);
        token.setStatus(CommonStatus.ACTIVE);
        return tokenRepository.save(token);
    }

}
