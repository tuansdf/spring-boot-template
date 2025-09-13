package com.example.sbt.infrastructure.config;

import com.example.sbt.common.constant.ApplicationProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JWTConfig {
    private final ApplicationProperties applicationProperties;

    @Bean
    public JWSHeader jwtHeader() {
        return new JWSHeader(JWSAlgorithm.HS256);
    }

    @Bean
    public JWSSigner jwtSigner() throws KeyLengthException {
        return new MACSigner(applicationProperties.getJwtSecret().getBytes());
    }

    @Bean
    public JWSVerifier jwsVerifier() throws JOSEException {
        return new MACVerifier(applicationProperties.getJwtSecret().getBytes());
    }
}
