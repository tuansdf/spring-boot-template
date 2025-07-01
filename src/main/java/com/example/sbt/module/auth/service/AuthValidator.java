package com.example.sbt.module.auth.service;

import com.example.sbt.shared.util.ConversionUtils;
import com.example.sbt.core.dto.LocaleKey;
import com.example.sbt.core.exception.CustomException;
import com.example.sbt.core.helper.LocaleHelper;
import com.example.sbt.core.helper.ValidationHelper;
import com.example.sbt.module.auth.dto.*;
import com.example.sbt.module.user.dto.ChangePasswordRequestDTO;
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
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
        if (StringUtils.isBlank(requestDTO.getUsername())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.username")));
        }
        String usernameError = validationHelper.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.email")));
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateLogin(LoginRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        if (StringUtils.isBlank(requestDTO.getUsername())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.username")));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(localeHelper.getMessage("form.error.over_max_length", new LocaleKey("field.username"), 255));
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateRequestResetPassword(RequestResetPasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.email")));
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateRequestActivateAccount(RequestActivateAccountRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.email")));
        }
        String emailError = validationHelper.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isEmpty(requestDTO.getToken())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.token")));
        }
        if (StringUtils.isEmpty(requestDTO.getNewPassword())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        String passwordError = validationHelper.validatePassword(requestDTO.getNewPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateChangePassword(ChangePasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(localeHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isEmpty(requestDTO.getOldPassword())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        if (StringUtils.isEmpty(requestDTO.getNewPassword())) {
            throw new CustomException(localeHelper.getMessage("form.error.required", new LocaleKey("field.password")));
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
