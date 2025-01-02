package org.tuanna.xcloneserver.controllers;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.entities.Token;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;
import org.tuanna.xcloneserver.modules.token.TokenService;
import org.tuanna.xcloneserver.utils.UUIDUtils;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("public")
public class PublicController {

    private final TokenService tokenService;

    @GetMapping("health")
    public Token check() {
        JWTPayload jwtPayload = JWTPayload.builder()
                .subjectId(UUIDUtils.generateId().toString())
                .permissions(Lists.newArrayList(PermissionCode.ADMIN))
                .build();
        Token token = tokenService.createJwtRefreshToken(jwtPayload);
        return token;
    }

}
