package org.tuanna.xcloneserver.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Envs {

    @Value("${custom.jwt-secret}")
    private String jwtSecret;
    @Value("${custom.jwt-access-lifetime}")
    private Integer jwtAccessLifetime;
    @Value("${custom.jwt-refresh-lifetime}")
    private Integer jwtRefreshLifetime;

}
