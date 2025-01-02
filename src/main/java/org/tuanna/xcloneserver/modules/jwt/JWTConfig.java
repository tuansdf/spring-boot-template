package org.tuanna.xcloneserver.modules.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuanna.xcloneserver.constants.Envs;

@RequiredArgsConstructor
@Configuration
public class JWTConfig {

    private final Envs envs;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC256(envs.getJwtSecret());
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

}
