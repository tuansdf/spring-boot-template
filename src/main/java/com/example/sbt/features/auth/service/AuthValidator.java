package com.example.sbt.features.auth.service;

import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.features.auth.dto.*;
import com.example.sbt.infrastructure.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthValidator {

    public void sanitizeRegister(RegisterRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setUsername(ConversionUtils.safeToString(requestDTO.getUsername()).trim());
        requestDTO.setEmail(ConversionUtils.safeToString(requestDTO.getEmail()).trim().toLowerCase());
        requestDTO.setName(ConversionUtils.safeToString(requestDTO.getName()).trim());
    }

    public void validateRegisterBusiness(RegisterRequest requestDTO) {
        if (RequestContextHolder.get().getTenantId() == null) {
            throw new CustomException("Missing tenant ID");
        }
    }

    public void sanitizeLogin(LoginRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setUsername(StringUtils.trimToNull(requestDTO.getUsername()));
    }

    public void sanitizeRequestResetPassword(RequestResetPasswordRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setEmail(StringUtils.trimToNull(requestDTO.getEmail()));
    }

    public void sanitizeRequestActivateAccount(RequestActivateAccountRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setEmail(StringUtils.trimToNull(requestDTO.getEmail()));
    }
}
