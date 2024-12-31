package org.tuanna.xcloneserver.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.dtos.JwtPayload;
import org.tuanna.xcloneserver.services.JwtService;
import org.tuanna.xcloneserver.utils.UUIDUtils;

import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("health")
public class HealthController {

    private final JwtService jwtService;

    @GetMapping
    public String check() {
        Instant now = Instant.now();
        Map<String, Object> claims = new HashMap<>();
        claims.put("sid", UUIDUtils.generateId().toString());
        claims.put("tid", UUIDUtils.generateId().toString());
        String token = jwtService.create(JwtPayload.builder()
                .expiresAt(now.plusSeconds(180))
                .issuedAt(now)
                .notBefore(now)
                .claims(claims)
                .build());
        System.out.println(token);
        DecodedJWT decodedJWT = jwtService.verify(token);
        System.out.println(decodedJWT);
        return "OK";
    }

}
