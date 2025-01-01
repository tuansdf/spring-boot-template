package org.tuanna.xcloneserver.modules.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tuanna.xcloneserver.constants.Envs;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Configuration
public class JwtConfig {

    private final Envs envs;
    private final ObjectMapper objectMapper;

    @Bean
    public JwtParser jwtParser() {
        byte[] keyBytes = envs.getJwtSecret().getBytes(StandardCharsets.UTF_8);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA256");
        return Jwts.parser().verifyWith(key).build();
    }

}
