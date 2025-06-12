package com.example.sbt.module.auth;

import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.ConversionUtils;
import com.example.sbt.common.util.LocaleHelper;
import com.example.sbt.common.util.LocaleHelper.LocaleKey;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthValidator {

    public void validateRegister(RegisterRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        requestDTO.setName(ConversionUtils.safeTrim(requestDTO.getName()));
        if (StringUtils.isBlank(requestDTO.getUsername())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.username")));
        }
        String usernameError = ValidationUtils.validateUsername(requestDTO.getUsername());
        if (usernameError != null) {
            throw new CustomException(usernameError);
        }
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.email")));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateLogin(LoginRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setUsername(ConversionUtils.safeTrim(requestDTO.getUsername()));
        if (StringUtils.isBlank(requestDTO.getUsername())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.username")));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.username"), 255));
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateForgotPassword(ForgotPasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        requestDTO.setEmail(ConversionUtils.safeTrim(requestDTO.getEmail()));
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.email")));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateRequestActivateAccount(RequestActivateAccountRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isBlank(requestDTO.getEmail())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.email")));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.request")));
        }
        if (StringUtils.isEmpty(requestDTO.getToken())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.token")));
        }
        if (StringUtils.isEmpty(requestDTO.getNewPassword())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.required", new LocaleKey("field.password")));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getNewPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

}
