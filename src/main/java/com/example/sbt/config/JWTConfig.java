package com.example.sbt.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.sbt.common.constant.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JWTConfig {

    private final ApplicationProperties applicationProperties;

    @Bean
    public Algorithm algorithm() {
        return Algorithm.HMAC256(applicationProperties.getJwtSecret());
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

}
