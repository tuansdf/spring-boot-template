package com.example.sbt.module.auth.service;

import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ValidationHelper;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;
import com.example.sbt.shared.util.ConversionUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthValidator {
    private final LocaleHelper localeHelper;
    private final ValidationHelper validationHelper;

    public void validateRegister(RegisterRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
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

    public void validateLogin(LoginRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        String usernameError = validationHelper.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateRequestResetPassword(RequestResetPasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateRequestActivateAccount(RequestActivateAccountRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("validation.error.missing", new LocaleKey("field.request")));
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) {
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

    public void validateChangePassword(ChangePasswordRequestDTO requestDTO) {
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
