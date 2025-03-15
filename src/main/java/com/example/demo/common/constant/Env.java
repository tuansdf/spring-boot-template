package com.example.demo.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Env {

    @Value("${custom.app-name}")
    private String applicationName;
    @Value("${custom.app-version}")
    private String applicationVersion;

    @Value("${custom.jwt-secret}")
    private String jwtSecret;
    @Value("${custom.jwt-access-lifetime}")
    private Integer jwtAccessLifetime;
    @Value("${custom.jwt-refresh-lifetime}")
    private Integer jwtRefreshLifetime;
    @Value("${custom.jwt-reset-password-lifetime}")
    private Integer jwtResetPasswordLifetime;
    @Value("${custom.jwt-activate-account-lifetime}")
    private Integer jwtActivateAccountLifetime;

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private Integer redisPort;

}
