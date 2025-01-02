package org.tuanna.xcloneserver.modules.token;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tuanna.xcloneserver.entities.Token;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public boolean validateTokenById(UUID id, String type) {
        if (id == null) {
            return false;
        }

        ZonedDateTime now = ZonedDateTime.now();

        Optional<Token> tokenOptional = tokenRepository.findById(id);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        Token token = tokenOptional.get();
        boolean isCorrectType = Strings.isNullOrEmpty(type) && type.equals(token.getType());
        boolean hasValue = !Strings.isNullOrEmpty(token.getValue());
        boolean isExpired = token.getExpiresAt().isAfter(now);
        return isCorrectType && hasValue && isExpired;
    }

}
