package org.tuanna.xcloneserver.utils;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.tuanna.xcloneserver.constants.PermissionCode;
import org.tuanna.xcloneserver.modules.auth.dtos.AuthenticationPrincipal;
import org.tuanna.xcloneserver.modules.jwt.dtos.JWTPayload;

import java.util.List;

public class AuthUtils {

    public static void setAuthentication(JWTPayload jwtPayload) {
        if (jwtPayload == null) {
            return;
        }
        List<String> permissions = PermissionCode.fromIndexes(jwtPayload.getPermissions());
        AuthenticationPrincipal principal = AuthenticationPrincipal.builder()
                .userId(jwtPayload.getSubjectId())
                .tokenId(jwtPayload.getTokenId())
                .permissions(permissions)
                .build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                permissions.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static AuthenticationPrincipal getAuthenticationPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticationPrincipal)) {
            return null;
        }
        return (AuthenticationPrincipal) authentication.getPrincipal();
    }

}
