package com.example.springboot.utils;

import com.example.springboot.constants.PermissionCode;
import com.example.springboot.modules.authentication.dtos.AuthenticationPrincipal;
import com.example.springboot.modules.jwt.dtos.JWTPayload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Slf4j
public class AuthUtils {

    public static void setAuthentication(JWTPayload jwtPayload) {
        if (jwtPayload == null) {
            return;
        }
        List<String> permissions = PermissionCode.fromIndexes(jwtPayload.getPermissions());
        AuthenticationPrincipal principal = AuthenticationPrincipal.builder()
                .userId(ConversionUtils.toUUID(jwtPayload.getSubject()))
                .permissions(permissions)
                .build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                permissions.stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static AuthenticationPrincipal getAuthenticationPrincipal() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return new AuthenticationPrincipal();
            }
            if (authentication.getPrincipal() instanceof AuthenticationPrincipal authenticationPrincipal) {
                return authenticationPrincipal;
            }
            return new AuthenticationPrincipal();
        } catch (Exception e) {
            return new AuthenticationPrincipal();
        }
    }

    public static boolean hasAnyPermission(List<String> permissions) {
        AuthenticationPrincipal principal = getAuthenticationPrincipal();
        if (CollectionUtils.isEmpty(principal.getPermissions())) {
            return false;
        }
        return principal.getPermissions().stream().anyMatch(permissions::contains);
    }

}
