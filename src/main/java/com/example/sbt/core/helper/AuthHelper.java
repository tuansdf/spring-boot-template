package com.example.sbt.core.helper;

import com.example.sbt.core.dto.RequestContext;
import com.example.sbt.core.dto.RequestContextData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthHelper {
    private final PasswordEncoder passwordEncoder;

    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    public boolean hasAnyPermission(String... permissions) {
        RequestContextData context = RequestContext.get();
        if (CollectionUtils.isEmpty(context.getPermissions())) {
            return false;
        }
        return Arrays.stream(permissions).anyMatch(x -> context.getPermissions().contains(x));
    }
}
