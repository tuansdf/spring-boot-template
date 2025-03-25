package com.example.sbt.module.auth;

import com.example.sbt.common.exception.CustomException;
import com.example.sbt.common.util.I18nHelper;
import com.example.sbt.common.util.ValidationUtils;
import com.example.sbt.module.auth.dto.ForgotPasswordRequestDTO;
import com.example.sbt.module.auth.dto.LoginRequestDTO;
import com.example.sbt.module.auth.dto.RegisterRequestDTO;
import com.example.sbt.module.auth.dto.ResetPasswordRequestDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthValidator {

    public void validateRegister(RegisterRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getUsername())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.username"));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.username", 255));
        }
        if (StringUtils.isEmpty(requestDTO.getEmail())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.email"));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.password"));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateLogin(LoginRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getUsername())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.username"));
        }
        if (requestDTO.getUsername().length() > 255) {
            throw new CustomException(I18nHelper.getMessageX("form.error.over_max_length", "field.username", 255));
        }
        if (StringUtils.isEmpty(requestDTO.getPassword())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.password"));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

    public void validateForgotPassword(ForgotPasswordRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getEmail())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.email"));
        }
        String emailError = ValidationUtils.validateEmail(requestDTO.getEmail());
        if (emailError != null) {
            throw new CustomException(emailError);
        }
    }

    public void validateResetPassword(ResetPasswordRequestDTO requestDTO) {
        if (StringUtils.isEmpty(requestDTO.getToken())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.token"));
        }
        if (StringUtils.isEmpty(requestDTO.getNewPassword())) {
            throw new CustomException(I18nHelper.getMessageX("form.error.missing", "field.password"));
        }
        String passwordError = ValidationUtils.validatePassword(requestDTO.getNewPassword());
        if (passwordError != null) {
            throw new CustomException(passwordError);
        }
    }

}
