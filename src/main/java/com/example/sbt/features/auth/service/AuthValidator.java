package com.example.sbt.features.auth.service;

import com.example.sbt.common.dto.LocaleKey;
import com.example.sbt.common.dto.RequestContextHolder;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.features.auth.dto.*;
import com.example.sbt.features.user.dto.ChangePasswordRequest;
import com.example.sbt.infrastructure.exception.CustomException;
import com.example.sbt.infrastructure.web.helper.LocaleHelper;
import com.example.sbt.infrastructure.web.helper.ValidationHelper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthValidator {
    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;

    public void sanitizeRegister(RegisterRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setUsername(ConversionUtils.safeToString(requestDTO.getUsername()).trim());
        requestDTO.setEmail(ConversionUtils.safeToString(requestDTO.getEmail()).trim().toLowerCase());
        requestDTO.setName(ConversionUtils.safeToString(requestDTO.getName()).trim());
    }

    public void validateRegister(RegisterRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        if (RequestContextHolder.get().getTenantId() == null) {
            throw new CustomException("Missing tenant ID");
        }
        String usernameError = validationHelper.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void sanitizeLogin(LoginRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setUsername(StringUtils.trimToNull(requestDTO.getUsername()));
    }

    public void validateLogin(LoginRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        String usernameError = validationHelper.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void sanitizeRequestResetPassword(RequestResetPasswordRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setEmail(StringUtils.trimToNull(requestDTO.getEmail()));
    }

    public void validateRequestResetPassword(RequestResetPasswordRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void sanitizeRequestActivateAccount(RequestActivateAccountRequest requestDTO) {
        if (requestDTO == null) return;
        requestDTO.setEmail(StringUtils.trimToNull(requestDTO.getEmail()));
    }

    public void validateRequestActivateAccount(RequestActivateAccountRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateResetPassword(ResetPasswordRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isEmpty(requestDTO.getToken())) {
            throw new CustomException(localeHelper.getMessage("validation.error.required", new LocaleKey("field.token")));
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getNewPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateChangePassword(ChangePasswordRequest requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getOldPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
        passwordError = validationHelper.validatePassword(requestDTO.getNewPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }
}
