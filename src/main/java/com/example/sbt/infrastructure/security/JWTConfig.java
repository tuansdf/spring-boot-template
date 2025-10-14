package com.example.sbt.infrastructure.security;

import com.example.sbt.infrastructure.web.config.CustomProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class JWTConfig {
    private final CustomProperties customProperties;

    @Bean
    public Map<String, JWSHeader> jwsHeaders() {
        return Map.of(
                JWTKeyID.ACCESS, new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(JWTKeyID.ACCESS).build(),
                JWTKeyID.REFRESH, new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(JWTKeyID.REFRESH).build(),
                JWTKeyID.OAUTH2, new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(JWTKeyID.OAUTH2).build(),
                JWTKeyID.PASSWORD_RESET, new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(JWTKeyID.PASSWORD_RESET).build(),
                JWTKeyID.ACCOUNT_ACTIVATION, new JWSHeader.Builder(JWSAlgorithm.HS256).keyID(JWTKeyID.ACCOUNT_ACTIVATION).build()
        );
    }

    @Bean
    public Map<String, JWSSigner> jwsSigners() throws KeyLengthException {
        return Map.of(
                JWTKeyID.ACCESS, new MACSigner(customProperties.getJwtAccessSecret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.REFRESH, new MACSigner(customProperties.getJwtRefreshSecret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.OAUTH2, new MACSigner(customProperties.getJwtOauth2Secret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.PASSWORD_RESET, new MACSigner(customProperties.getJwtPasswordResetSecret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.ACCOUNT_ACTIVATION, new MACSigner(customProperties.getJwtAccountActivationSecret().getBytes(StandardCharsets.UTF_8))
        );
    }

    @Bean
    public Map<String, JWSVerifier> jwsVerifiers() throws JOSEException {
        return Map.of(
                JWTKeyID.ACCESS, new MACVerifier(customProperties.getJwtAccessSecret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.REFRESH, new MACVerifier(customProperties.getJwtRefreshSecret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.OAUTH2, new MACVerifier(customProperties.getJwtOauth2Secret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.PASSWORD_RESET, new MACVerifier(customProperties.getJwtPasswordResetSecret().getBytes(StandardCharsets.UTF_8)),
                JWTKeyID.ACCOUNT_ACTIVATION, new MACVerifier(customProperties.getJwtAccountActivationSecret().getBytes(StandardCharsets.UTF_8))
        );
    }

    @Bean
    public Map<String, JWTLifetime> jwtLifetimes() {
        return Map.of(
                JWTKeyID.ACCESS, new JWTLifetime(customProperties.getJwtAccessLifetime()),
                JWTKeyID.REFRESH, new JWTLifetime(customProperties.getJwtRefreshLifetime()),
                JWTKeyID.OAUTH2, new JWTLifetime(customProperties.getJwtOauth2Lifetime()),
                JWTKeyID.PASSWORD_RESET, new JWTLifetime(customProperties.getJwtPasswordResetLifetime()),
                JWTKeyID.ACCOUNT_ACTIVATION, new JWTLifetime(customProperties.getJwtAccountActivationLifetime())
        );
    }
}
