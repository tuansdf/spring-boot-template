package org.tuanna.xcloneserver.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Env {

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

    @Value("${custom.email-from}")
    private String fromEmail;

}
