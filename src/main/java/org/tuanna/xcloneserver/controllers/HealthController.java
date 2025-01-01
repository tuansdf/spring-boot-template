package org.tuanna.xcloneserver.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.modules.jwt.JwtService;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("health")
public class HealthController {

    private final JwtService jwtService;

    @GetMapping
    public String check() {
        Instant now = Instant.now();
        AccessJwtPayload token = new AccessJwtPayload();
        token.setExpiresAt(now.plusSeconds(180));
        token.setIssuedAt(now);
        token.setNotBefore(now);
        token.setSubjectId(UUIDUtils.generateId().toString());
        String jwt = jwtService.create(token);
        log.info(jwt);
        DecodedJWT decodedJWT = jwtService.verify(jwt);
        log.info("decoded: {}", decodedJWT);
        return "OK";
    }

}
