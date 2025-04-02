package com.example.sbt.module.auth;

import com.example.sbt.common.exception.CustomException;
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
        if (StringUtils.isEmpty(requestDTO.getUsername())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.username")));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.username"), 255));
        }
        if (StringUtils.isEmpty(requestDTO.getEmail())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.email")));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.password")));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateLogin(LoginRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getUsername())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.username")));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(LocaleHelper.getMessage("form.error.over_max_length", new LocaleKey("field.username"), 255));
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.password")));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateForgotPassword(ForgotPasswordRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getEmail())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.email")));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateRequestActivateAccount(RequestActivateAccountRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getEmail())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.email")));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getToken())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.token")));
        }
        if (StringUtils.isEmpty(requestDTO.getNewPassword())) {
            throw new CustomException(LocaleHelper.getMessage("form.error.missing", new LocaleKey("field.password")));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getNewPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

}
